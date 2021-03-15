package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer.PathAnalyzer;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer.PathExtractor;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.BaseCFGPath;
import soot.Body;
import soot.SootMethod;
import soot.Unit;

import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:50
 */
public class ResourceLeakDetector {
    public static boolean detect(SootMethod sootMethod, Unit unit) {
        List<BaseCFGPath> paths = PathExtractor.extractPath(unit, sootMethod);
        return new PathAnalyzer(sootMethod, paths).analyze();
    }
}