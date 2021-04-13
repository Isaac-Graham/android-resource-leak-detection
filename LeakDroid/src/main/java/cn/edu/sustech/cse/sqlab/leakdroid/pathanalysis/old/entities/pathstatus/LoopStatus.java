package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.entities.pathstatus;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import org.apache.log4j.Logger;
import soot.Unit;
import soot.jimple.InvokeStmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/19 21:25
 */
@Deprecated
public class LoopStatus extends BasePathStatus implements Cloneable {
    private static final Logger logger = Logger.getLogger(LoopStatus.class);
    private final Loop currentLoop;

    public LoopStatus(Loop currentLoop) {
        this.currentLoop = currentLoop;
    }

    @Override
    public Object clone() {
        return (LoopStatus) super.clone();
    }

    @Override
    public void addPath(Unit unit) {
        this.pathStack.push(unit);
        Stack<Unit> successors = new Stack<>();
        if (unit == null) {
            this.neighborStack.push(successors);
        } else if (currentLoop.getBackJumpStmt() == unit) {
            this.neighborStack.push(new Stack<>());
        } else {
            ExceptionalUnitGraph cfg = ICFGContext.getCFGFromUnit(unit);
            cfg.getSuccsOf(unit).forEach(successor -> {
                if (ResourceUtil.isRequest(unit) && UnitUtil.isCaughtExceptionRef(successor)) return;
                if (!(unit instanceof InvokeStmt) && UnitUtil.isCaughtExceptionRef(successor)) return;
//                if (currentLoop.getHead() == successor) return;
                if (currentLoop.getLoopStatements().contains(successor)) {
                    successors.push(successor);
                }
            });
            this.neighborStack.push(successors);
        }
    }
}
