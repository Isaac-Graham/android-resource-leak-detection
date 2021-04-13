package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.utils.pathutils;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.entities.cfgpath.LoopExitCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.entities.pathstatus.LoopStatus;
import cn.edu.sustech.cse.sqlab.leakdroid.util.LoopUtil;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 19:35
 */
@Deprecated
public class LoopExitPathUtil extends BasePathUtil implements Cloneable {
    private Loop currentLoop;

    public LoopExitPathUtil(Loop currentLoop) {
        this.currentLoop = currentLoop;
        super.pathStatus = new LoopStatus(currentLoop);
        super.cfgPath = new LoopExitCFGPath(currentLoop);
    }

    public LoopExitPathUtil(Unit startUnit, Loop currentLoop) {
        this(currentLoop);
        this.updatePath(startUnit);
    }

    @Override
    public List<BasePathUtil> runPath() {
        List<BasePathUtil> basePathUtils = super.runPath();
        return addTargetOfExitStmt(basePathUtils);
    }

    @Override
    protected void dealLoop(Unit nextUnit, List<BasePathUtil> basePathUtils) {
        List<BasePathUtil> loopsPathUtils = LoopUtil.getLoopPaths(nextUnit);
        List<BasePathUtil> mergedPathUtils = this.mergePathUtils(loopsPathUtils);
        for (BasePathUtil mergedPathUtil : mergedPathUtils) {
            Unit tail = mergedPathUtil.getPathTail();
            mergedPathUtil.callBack();
            if (mergedPathUtil.cfgPath.getPath().contains(tail)) {
                basePathUtils.add(mergedPathUtil);
            } else {
                basePathUtils.addAll(mergedPathUtil.mergePathUtils(new LoopExitPathUtil(tail, currentLoop).runPath()));
            }
        }
    }

    private List<BasePathUtil> addTargetOfExitStmt(List<BasePathUtil> basePathUtils) {
        List<BasePathUtil> res = new ArrayList<>();
        basePathUtils.forEach(basePathUtil -> {
            Unit loopExit = basePathUtil.getPathTail();
            Collection<Stmt> targets = currentLoop.targetsOfLoopExit((Stmt) loopExit);
            if (targets.isEmpty()) {
                BasePathUtil newBasePathUtil = (BasePathUtil) basePathUtil.clone();
                res.add(newBasePathUtil);
            } else {
                targets.forEach(target -> {
                    BasePathUtil newBasePathUtil = (BasePathUtil) basePathUtil.clone();
                    newBasePathUtil.updatePath(target);
                    res.add(newBasePathUtil);
                });
            }

        });
        return res;
    }

    @Override
    public Object clone() {
        return (LoopExitPathUtil) super.clone();
    }
}
