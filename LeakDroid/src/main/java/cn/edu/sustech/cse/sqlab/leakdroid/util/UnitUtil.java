package cn.edu.sustech.cse.sqlab.leakdroid.util;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.BaseCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.UnitMethodNameTag;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.AbstractDefinitionStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.shimple.PhiExpr;
import soot.tagkit.Tag;
import soot.util.Chain;

import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/18 15:46
 */
public class UnitUtil {
    private static final Logger logger = Logger.getLogger(UnitUtil.class);
    public static final String leftOp = "leftOp";
    public static final String rightOp = "rightOp";

    public static SootMethod getSootMethod(Unit unit) {
        List<Tag> tags = unit.getTags();
        for (Tag tag : tags) {
            if (tag instanceof UnitMethodNameTag) {
                return ((UnitMethodNameTag) tag).getSootMethod();
            }
        }
        logger.warn(String.format("No soot method found in unit: %s", unit));
        return null;
    }

    public static Body getBody(Unit unit) {
        SootMethod sootMethod = getSootMethod(unit);
        if (sootMethod == null) {
            return null;
        }
        return sootMethod.getActiveBody();
    }

    public static void addMethodTag(Chain<SootClass> sootClasses) {
        sootClasses.forEach(sootClass -> {
            sootClass.getMethods().forEach(sootMethod -> {
                if (!sootMethod.hasActiveBody()) return;
                SootMethodUtil.ensureSSA(sootMethod);
                SootMethodUtil.updateLocalName(sootMethod);
                Body body = sootMethod.getActiveBody();
                body.getUnits().forEach(unit -> {
                    unit.addTag(new UnitMethodNameTag(sootMethod));
                });
            });
        });
    }

    public static ResourceLeakTag getResourceLeakTag(Unit unit) {
        if (!unit.hasTag(ResourceLeakTag.name)) return null;
        return (ResourceLeakTag) unit.getTag(ResourceLeakTag.name);
    }

    public static boolean isCaughtExceptionRef(Unit unit) {
        if (unit instanceof DefinitionStmt) {
            DefinitionStmt stmt = (DefinitionStmt) unit;
            return stmt.getRightOp() instanceof CaughtExceptionRef;
        }
        return false;
    }

    public static Value getInvokeBase(Unit unit) {
        if (!(unit instanceof InvokeStmt)) {
            logger.warn(String.format("Unit is not invoke stmt. unit: %s, unit class: %s", unit, unit.getClass()));
            return null;
        }
        InvokeStmt invokeStmt = (InvokeStmt) unit;
        InvokeExpr invokeExpr = invokeStmt.getInvokeExpr();
        if (invokeExpr instanceof InstanceInvokeExpr) {
            InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
            return instanceInvokeExpr.getBase();
        }
        return null;
    }

    public static Value getDefineOp(Unit unit, String opSide) {
        if (!(unit instanceof DefinitionStmt)) {
            logger.warn(String.format("Unit is not define statement. unit: %s, unit class: %s", unit, unit.getClass()));
            return null;
        }
        DefinitionStmt defineStmt = (DefinitionStmt) unit;
        Value res = null;
        if (opSide.equals(leftOp)) {
            res = defineStmt.getLeftOp();
        } else {
            res = defineStmt.getRightOp();
        }
        return res;
    }

    public static Value getPhiValue(Value phi, List<Unit> path) {
        if (!(phi instanceof PhiExpr)) {
            logger.warn(String.format("Value is not phiExpr. value: %s, value class: %s", phi, phi.getClass()));
            return null;
        }
        if (path == null) {
            return null;
        }
        PhiExpr phiExpr = (PhiExpr) phi;
        Value res = null;
        for (int i = path.size() - 1; i >= 0; i--) {
            res = phiExpr.getValue(path.get(i));
            if (res != null) {
                break;
            }
        }
        if (res == null) {
            logger.warn(String.format("Cannot get value from phiExpr. Phi: %s, path: %s", phi, path));
        }
        return res;
    }

}
