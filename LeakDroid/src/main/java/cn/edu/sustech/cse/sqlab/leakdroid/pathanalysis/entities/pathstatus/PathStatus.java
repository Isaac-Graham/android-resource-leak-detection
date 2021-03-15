package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.pathstatus;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import org.apache.log4j.Logger;
import soot.Unit;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:26
 */
public class PathStatus extends BasePathStatus implements Cloneable {
    private static Logger logger = Logger.getLogger(PathStatus.class);

    @Override
    public Object clone() {
        return (PathStatus) super.clone();
    }

    @Override
    public void addPath(Unit unit) {
        this.pathStack.push(unit);
        Stack<Unit> successors = new Stack<>();
        if (unit != null) {
            ExceptionalUnitGraph cfg = ICFGContext.getCFGFromUnit(unit);
            cfg.getSuccsOf(unit).forEach(successors::push);
        }
        this.neighborStack.push(successors);
    }
}
