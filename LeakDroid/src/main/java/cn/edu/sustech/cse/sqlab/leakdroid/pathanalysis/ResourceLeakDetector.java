package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis;

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
        List<CFGPath> paths = PathExtractor.extractPath(unit);
        return new PathAnalyzer(unit, paths, meetMethods).analyze();
    }

    public static boolean detect(Unit unit) {
        return detect(unit, new HashSet<>());
    }
}