package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.utils.pathutils;

import soot.Unit;
import soot.jimple.toolkits.annotation.logic.Loop;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/9 14:51
 */
@Deprecated
public class LoopPathUtil extends BasePathUtil implements Cloneable {
    private final LoopOncePathUtil loopOncePathUtil;
    private final LoopExitPathUtil loopExitPathUtil;


    public LoopPathUtil(Unit unit, Loop currentLoop) {
        loopOncePathUtil = new LoopOncePathUtil(unit, currentLoop);
        loopExitPathUtil = new LoopExitPathUtil(unit, currentLoop);
    }

    public List<BasePathUtil> runPath() {
        List<BasePathUtil> loopOncePaths = loopOncePathUtil.runPath();
        List<BasePathUtil> loopExitPaths = loopExitPathUtil.runPath();
        List<BasePathUtil> res = new ArrayList<>();
        loopOncePaths.forEach(loopOncePath -> {
            loopExitPaths.forEach(loopExitPath -> {
                res.add(loopOncePath.mergePathUtil(loopExitPath));
            });
        });
        return res;
    }

    @Override
    protected void dealLoop(Unit nextUnit, List<BasePathUtil> basePathUtils) {
        // ignore
    }

    @Override
    public Object clone() {
        return (LoopPathUtil) super.clone();
    }

    protected void updatePath(Unit nextUnit) {
        this.pathStatus.addPath(nextUnit);
        this.cfgPath.addPath(nextUnit);
    }
}
