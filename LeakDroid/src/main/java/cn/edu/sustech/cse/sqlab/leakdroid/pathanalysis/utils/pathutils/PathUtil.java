package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.pathutils;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.CFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.LoopExitCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.pathstatus.LoopExitPathStatus;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.pathstatus.PathStatus;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.LoopUtil;
import soot.Unit;
import soot.jimple.toolkits.annotation.logic.Loop;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 23:04
 */
public class PathUtil extends BasePathUtil implements Cloneable {
    public PathUtil() {
        this.pathStatus = new PathStatus();
        this.cfgPath = new CFGPath();
    }

    public PathUtil(Unit startUnit) {
        this();
        this.updatePath(startUnit);
    }

    @Override
    protected void dealLoop(Unit nextUnit, List<BasePathUtil> basePathUtils) {
        List<BasePathUtil> loopsPathUtils = LoopUtil.getLoopPaths(nextUnit);
        List<BasePathUtil> mergedPathUtils = this.mergePathUtils(loopsPathUtils);
        for (BasePathUtil mergedPathUtil : mergedPathUtils) {
            Unit tail = mergedPathUtil.getPathTail();
            mergedPathUtil.callBack();
            basePathUtils.addAll(mergedPathUtil.mergePathUtils(new PathUtil(tail).runPath()));
        }
    }

    @Override
    public Object clone() {
        return (PathUtil) super.clone();
    }
}
