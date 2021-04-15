package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.utils.pathutils;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.entities.cfgpath.LoopOnceCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.entities.pathstatus.LoopStatus;
import cn.edu.sustech.cse.sqlab.leakdroid.util.LoopUtil;
import soot.Unit;
import soot.jimple.toolkits.annotation.logic.Loop;

import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 17:15
 */
@Deprecated
public class LoopOncePathUtil extends BasePathUtil implements Cloneable {
    private final Loop currentLoop;

    public LoopOncePathUtil(Loop currentLoop) {
        this.currentLoop = currentLoop;
        super.pathStatus = new LoopStatus(currentLoop);
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
                basePathUtils.add(mergedPathUtil);
            } else {
                basePathUtils.addAll(mergedPathUtil.mergePathUtils(new LoopOncePathUtil(tail, currentLoop).runPath()));
            }
        }
    }
}