package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.entities.pathstatus;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import org.apache.log4j.Logger;
import soot.Unit;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:26
 */
@Deprecated
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
            cfg.getSuccsOf(unit).forEach(successor -> {
                if (ResourceUtil.isRequest(unit) && UnitUtil.isCaughtExceptionRef(successor)) return;
                if (!(unit instanceof InvokeStmt) && UnitUtil.isCaughtExceptionRef(successor)) return;
                successors.push(successor);
            });
        }
        this.neighborStack.push(successors);
    }
}
