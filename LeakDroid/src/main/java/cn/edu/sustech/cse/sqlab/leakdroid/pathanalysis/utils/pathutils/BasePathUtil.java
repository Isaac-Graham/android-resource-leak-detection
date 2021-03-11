package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.pathutils;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.BaseCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.pathstatus.BasePathStatus;
import soot.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 19:01
 */
public abstract class BasePathUtil implements Cloneable {
    protected BasePathStatus pathStatus;
    protected BaseCFGPath cfgPath;

    @Override
    public Object clone() {
        BasePathUtil basePathUtil = null;
        try {
            basePathUtil = (BasePathUtil) super.clone();
            basePathUtil.pathStatus = (BasePathStatus) pathStatus.clone();
            basePathUtil.cfgPath = (BaseCFGPath) cfgPath.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return basePathUtil;
    }

    public abstract List<BasePathUtil> runPath();

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

    public BaseCFGPath getCFGPath() {
        return cfgPath;
    }

    protected List<BasePathUtil> mergePathUtils(List<BasePathUtil> paths) {
        List<BasePathUtil> mergedPaths = new ArrayList<>();
        paths.forEach(path -> {
            mergedPaths.add(this.mergePathUtil(path));
        });
        return mergedPaths;
    }

    protected BasePathUtil mergePathUtil(BasePathUtil basePathUtil) {
        BasePathUtil res = (BasePathUtil) this.clone();
        res.pathStatus.mergePathStatus(basePathUtil.pathStatus);
        res.cfgPath.mergeCFGPath(basePathUtil.cfgPath);
        return res;
    }

    public String toString() {
        return String.format("[%s]_[%s]", pathStatus, cfgPath);
    }

    public Unit getPathTail() {
        return cfgPath.getPathTail();
    }

    public void clearPathStatus() {
        this.pathStatus.clearNeighborStack();
    }
}
