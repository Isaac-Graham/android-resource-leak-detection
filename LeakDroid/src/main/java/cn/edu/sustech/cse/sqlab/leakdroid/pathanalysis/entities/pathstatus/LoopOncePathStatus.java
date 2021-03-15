package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.pathstatus;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import soot.Unit;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 19:21
 */
public class LoopOncePathStatus extends BasePathStatus implements Cloneable {
    private Loop currentLoop;

    public LoopOncePathStatus(Loop currentLoop) {
        this.currentLoop = currentLoop;
    }

    @Override
    public Object clone() {
        return (LoopOncePathStatus) super.clone();
    }

    @Override
    public void addPath(Unit unit) {
        this.pathStack.push(unit);
        if (unit == null) {
            this.neighborStack.push(new Stack<>());
        } else if (currentLoop.getBackJumpStmt() == unit) {
            this.neighborStack.push(new Stack<>());
        } else {
            Stack<Unit> successors = new Stack<>();
            ExceptionalUnitGraph cfg = ICFGContext.getCFGFromUnit(unit);
            cfg.getSuccsOf(unit).forEach(successor -> {
                if (currentLoop.getLoopStatements().contains(successor)) {
                    successors.push(successor);
                }
            });
            this.neighborStack.push(successors);
        }
    }
}
