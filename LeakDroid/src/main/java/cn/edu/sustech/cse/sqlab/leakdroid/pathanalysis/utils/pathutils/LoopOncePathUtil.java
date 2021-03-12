package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.pathutils;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.LoopOnceCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.pathstatus.LoopOncePathStatus;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.LoopUtil;
import soot.Unit;
import soot.jimple.toolkits.annotation.logic.Loop;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 17:15
 */
public class LoopOncePathUtil extends BasePathUtil implements Cloneable {
    private Loop currentLoop;

    public LoopOncePathUtil(Loop currentLoop) {
        this.currentLoop = currentLoop;
        super.pathStatus = new LoopOncePathStatus(currentLoop);
        super.cfgPath = new LoopOnceCFGPath(currentLoop);
    }

    public LoopOncePathUtil(Unit startUnit, Loop currentLoop) {
        this(currentLoop);
        this.updatePath(startUnit);
    }


    @Override
    protected void dealLoop(Unit nextUnit, List<BasePathUtil> basePathUtils) {
        List<BasePathUtil> loopsPathUtils = LoopUtil.getLoopPaths(nextUnit);
        List<BasePathUtil> mergedPathUtils = this.mergePathUtils(loopsPathUtils);
        for (BasePathUtil mergedPathUtil : mergedPathUtils) {
            Unit tail = mergedPathUtil.getPathTail();
            mergedPathUtil.callBack();
            // The
            if (mergedPathUtil.cfgPath.getPath().contains(tail)) {
                continue;
            }
            basePathUtils.addAll(mergedPathUtil.mergePathUtils(new LoopOncePathUtil(tail, currentLoop).runPath()));
        }
    }
}
