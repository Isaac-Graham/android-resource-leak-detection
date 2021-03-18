package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer.PathAnalyzer;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer.PathExtractor;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.BaseCFGPath;
import soot.Body;
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
    public final Unit unit;
    public final Set<SootMethod> meetMethods;

    public ResourceLeakDetector(Unit unit) {
        this.unit = unit;
        this.meetMethods = new HashSet<>();
    }

    public ResourceLeakDetector(Unit unit, Set<SootMethod> meetMethods) {
        this.unit = unit;
        this.meetMethods = meetMethods;
    }

    public boolean detect() {
        List<BaseCFGPath> paths = PathExtractor.extractPath(unit);
        return new PathAnalyzer(paths, unit, meetMethods).analyze();
    }
}