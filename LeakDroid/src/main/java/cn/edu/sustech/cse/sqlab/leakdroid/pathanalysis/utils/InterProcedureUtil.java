package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ResourceLeakDetector;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.InvokeStmt;
import soot.jimple.ParameterRef;
import soot.jimple.internal.AbstractDefinitionStmt;
import soot.jimple.internal.JIdentityStmt;

import java.util.List;
import java.util.Set;

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

    public static boolean dealInterProcedureCall(Unit unit, Set<Value> localValuables) {
        InvokeStmt invokeStmt = (InvokeStmt) unit;
        int argIndex = getInterProcedureParameterIndex(invokeStmt, localValuables);
        SootMethod invokeMethod = invokeStmt.getInvokeExpr().getMethod();
        Body body = invokeMethod.getActiveBody();
        Unit startUnit = getStartUnit(body, argIndex);
        return ResourceLeakDetector.detect(startUnit);
    }

    public static Unit getStartUnit(Body body, int argIndex) {
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
