package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.pathstatus;

import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import soot.Unit;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 16:46
 */
public class LoopPathStatus extends BasePathStatus implements Cloneable {
    private Loop currentLoop;


    public LoopPathStatus(Loop currentLoop) {
        super();
        this.currentLoop = currentLoop;
    }

    @Override
    public Object clone() {
        return (LoopPathStatus) super.clone();
    }

    @Override
    public void addPath(Unit unit) {
        this.pathStack.push(unit);
        if (unit == null) {
            this.neighborStack.push(new Stack<>());
        } else {
            Stack<Unit> successors = new Stack<>();
            ExceptionalUnitGraph cfg = ICFGContext.cfgGraphs.get(ICFGContext.icfg.getBodyOf(unit));
            cfg.getSuccsOf(unit).forEach(successor -> {
                if (currentLoop.getLoopStatements().contains(successor)) {
                    successors.push(successor);
                }
            });
            this.neighborStack.push(successors);
        }
    }
}
