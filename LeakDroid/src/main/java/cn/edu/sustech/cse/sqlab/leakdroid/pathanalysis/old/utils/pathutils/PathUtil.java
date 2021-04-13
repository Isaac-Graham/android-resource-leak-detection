package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.utils.pathutils;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.entities.cfgpath.CFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.entities.pathstatus.PathStatus;
import cn.edu.sustech.cse.sqlab.leakdroid.util.LoopUtil;
import soot.Unit;

import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 23:04
 */
@Deprecated
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
            if (mergedPathUtil.getCFGPath().getPath().contains(tail)) continue;
            basePathUtils.addAll(mergedPathUtil.mergePathUtils(new PathUtil(tail).runPath()));
        }
    }

    @Override
    public Object clone() {
        return (PathUtil) super.clone();
    }
}
