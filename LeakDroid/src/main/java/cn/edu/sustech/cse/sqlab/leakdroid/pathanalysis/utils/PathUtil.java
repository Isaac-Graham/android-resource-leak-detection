package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.CFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.PathStatus;
import soot.Unit;
import soot.coffi.CFG;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/9 14:51
 */
public class PathUtil implements Cloneable {
    protected PathStatus pathStatus;
    protected CFGPath cfgPath;

    public PathUtil() {
        this.pathStatus = new PathStatus();
        this.cfgPath = new CFGPath();
    }

    //
//
//    public PathUtil(PathUtil util) {
//        this.cfgPath = new CFGPath(util.getCFGPath());
//        this.pathStatus = new PathStatus();
//        // TODO: update pathStatus
//        for (int i = 0; i < cfgPath.getLength(); i++) {
//            this.pathStatus.addPath(null);
//        }
//    }
    @Override
    public Object clone() {
        PathUtil pathUtils = null;
        try {
            pathUtils = (PathUtil) super.clone();
            pathUtils.pathStatus = (PathStatus) pathStatus.clone();
            pathUtils.cfgPath = (CFGPath) cfgPath.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return pathUtils;
    }

    public PathUtil(Unit startUnit) {
        this();
        this.updatePath(startUnit);
    }


    public List<PathUtil> runPath() {
        List<PathUtil> pathUtils = new ArrayList<>();
        while (!pathStatus.isEnd()) {
            Stack<Unit> neighborStackTop = pathStatus.getNeighborTop();
            if (neighborStackTop.empty()) {
                if (cfgPath.isEnd()) {
                    pathUtils.add((PathUtil) this.clone());
                }
                callBack();
            } else {
                Unit nextUnit = neighborStackTop.pop();
                if (LoopUtil.isLoopHead(nextUnit)) {
                    List<PathUtil> loopPaths = new LoopPathUtil(nextUnit).runPath();
                    pathUtils.addAll(this.mergePath(loopPaths));
                    break;
                } else {
                    this.updatePath(nextUnit);
                }
            }
        }
        return pathUtils;
    }

    protected void callBack() {
        this.pathStatus.callBack();
        this.cfgPath.callBack();
    }

    protected void updatePath(Unit nextUnit) {
        this.pathStatus.addPath(nextUnit);
        this.cfgPath.addPath(nextUnit);
    }

    public boolean isEmpty() {
        return cfgPath.isEmpty();
    }

    public boolean isFinished() {
        return cfgPath.isEnd();
    }

    public CFGPath getCFGPath() {
        return cfgPath;
    }

    protected List<PathUtil> mergePath(List<PathUtil> paths) {
        List<PathUtil> mergedPaths = new ArrayList<>();
        paths.forEach(path -> {
            PathUtil newPathUtil = (PathUtil) this.clone();
            newPathUtil.mergePathUtil(path);
            mergedPaths.add(newPathUtil);
        });
        return mergedPaths;
    }

    protected void mergePathUtil(PathUtil pathUtil) {
        this.pathStatus.mergePathStatus(pathUtil.pathStatus);
        this.cfgPath.mergeCFGPath(pathUtil.cfgPath);
    }

}
