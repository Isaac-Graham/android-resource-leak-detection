package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer.PathAnalyzer;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.pathextractor.CFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.pathextractor.PathExtractor;
import org.apache.log4j.Logger;
import soot.SootMethod;
import soot.Unit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:50
 */
public class ResourceLeakDetector {
    private static final Logger logger = Logger.getLogger(ResourceLeakDetector.class);

    public static boolean detect(Unit unit, Set<SootMethod> meetMethods) {
        PathExtractor extractor = new PathExtractor();
        PathAnalyzer analyzer = new PathAnalyzer(unit, meetMethods);


        DaemonThread daemonThread = new DaemonThread(extractor, analyzer);
        daemonThread.setDaemon(true);
        daemonThread.start();

        List<CFGPath> paths = extractor.extractPath(unit);
        return analyzer.analyze(paths);

    }

    public static boolean detect(Unit unit) {
        return detect(unit, new HashSet<>());
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