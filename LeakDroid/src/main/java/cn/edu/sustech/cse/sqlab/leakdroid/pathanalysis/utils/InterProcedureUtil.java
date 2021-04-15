package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils;

import cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ResourceLeakDetector;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.InvokeStmt;
import soot.jimple.ParameterRef;
import soot.jimple.internal.AbstractDefinitionStmt;
import soot.jimple.internal.JIdentityStmt;

import java.util.List;
import java.util.Set;

import static cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier.NOT_LEAK;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/13 20:59
 */
public class InterProcedureUtil {
    private static final Logger logger = Logger.getLogger(InterProcedureUtil.class);

    public static boolean isInterProcedureCall(Unit unit, Set<Value> localValuables) {
        if (!(unit instanceof InvokeStmt)) {
            return false;
        }
        InvokeStmt invokeStmt = (InvokeStmt) unit;

        // TODO: 考虑一下instance.invoke??
        return getInterProcedureParameterIndex(invokeStmt, localValuables) != -1;
    }

    private static int getInterProcedureParameterIndex(InvokeStmt invokeStmt, Set<Value> localValuables) {
        List<Value> args = invokeStmt.getInvokeExpr().getArgs();
        for (int i = 0; i < args.size(); i++) {
            if (localValuables.contains(args.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static SootMethod getInvokeMethod(Unit unit) {
        if (!(unit instanceof InvokeStmt)) {
            return null;
        }
        InvokeStmt invokeStmt = (InvokeStmt) unit;
        return invokeStmt.getInvokeExpr().getMethod();
    }

    public static LeakIdentifier dealInterProcedureCall(Unit unit, Set<Value> localValuables, Set<SootMethod> meetMethods) {
        InvokeStmt invokeStmt = (InvokeStmt) unit;
        int argIndex = getInterProcedureParameterIndex(invokeStmt, localValuables);
        SootMethod invokeMethod = invokeStmt.getInvokeExpr().getMethod();
        if (ICFGContext.getSootMethodArgLeakIdentifier(invokeMethod, argIndex) == null) {
            if (!invokeMethod.hasActiveBody()) return NOT_LEAK;
            Body body = invokeMethod.getActiveBody();
            Unit startUnit = getStartUnit(body, argIndex);
            LeakIdentifier identifier = ResourceLeakDetector.detect(startUnit, meetMethods);
            ICFGContext.setSootMethodArgLeakIdentifier(invokeMethod, argIndex, identifier);
        }
        return ICFGContext.getSootMethodArgLeakIdentifier(invokeMethod, argIndex);
    }

    private static Unit getStartUnit(Body body, int argIndex) {
        for (Unit unit : body.getUnits()) {
            if (unit instanceof JIdentityStmt) {
                AbstractDefinitionStmt stmt = (AbstractDefinitionStmt) unit;
                if (stmt.getRightOp() instanceof ParameterRef) {
                    ParameterRef ref = (ParameterRef) stmt.getRightOp();
                    if (ref.getIndex() == argIndex) {
                        return unit;
                    }
                }
            }
        }
        return null;
    }
}
