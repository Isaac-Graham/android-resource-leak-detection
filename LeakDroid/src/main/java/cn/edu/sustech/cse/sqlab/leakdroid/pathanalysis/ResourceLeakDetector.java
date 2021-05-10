package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer.PathAnalyzer;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.pathextractor.CFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.pathextractor.PathExtractor;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import org.apache.log4j.Logger;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.ThisRef;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:50
 */
public class ResourceLeakDetector {
    private static final Logger logger = Logger.getLogger(ResourceLeakDetector.class);

    public static LeakIdentifier detect(Unit unit, Set<SootMethod> meetMethods) {
        return detect(Collections.singletonList(unit),
                UnitUtil.getBody(unit),
                meetMethods,
                true);
    }


    public static LeakIdentifier detect(Body body, Set<SootMethod> meetMethods) {
        return detect(body.getUnits()
                        .parallelStream()
                        .filter(ResourceUtil::isRequest)
                        .collect(Collectors.toList()),
                body,
                meetMethods,
                false);
    }

    private static LeakIdentifier detect(List<Unit> requestedUnits,
                                         Body body,
                                         Set<SootMethod> meetMethods,
                                         boolean interProcedural) {
        Set<Value> fieldValues = SootMethodUtil.getFieldValue(body.getMethod());
        PathExtractor extractor = new PathExtractor();
        PathAnalyzer analyzer = new PathAnalyzer(body.getMethod(), meetMethods, fieldValues);

        if (!OptionsArgs.debugMode) {
            DaemonThread daemonThread = new DaemonThread(extractor, analyzer);
            daemonThread.setDaemon(true);
            daemonThread.start();
        }


        List<CFGPath> paths = new ArrayList<>();
        requestedUnits.forEach(unit -> {
            paths.addAll(extractor.extractPath(unit));
        });
        Collections.sort(paths);

        return analyzer.analyze(paths, interProcedural);
    }

    private static class DaemonThread extends Thread {
        final PathExtractor extractor;
        final PathAnalyzer analyzer;

        DaemonThread(PathExtractor extractor, PathAnalyzer analyzer) {
            this.extractor = extractor;
            this.analyzer = analyzer;
        }

        @Override
        public void run() {
            try {
                sleep(OptionsArgs.timeLimit * 1000L);
                extractor.isEnd = true;
                analyzer.isEnd = true;
            } catch (InterruptedException e) {
            }
        }
    }
}