package cn.edu.sustech.cse.sqlab.leakdroid.util;

import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.UnitMethodNameTag;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.IdentityStmt;
import soot.jimple.internal.AbstractDefinitionStmt;
import soot.jimple.internal.JIdentityStmt;
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

    public static void addMethodTag(Chain<SootClass> sootClasses) {
        sootClasses.forEach(sootClass -> {
            sootClass.getMethods().forEach(sootMethod -> {
                if (!sootMethod.hasActiveBody()) return;
                SootMethodUtil.ensureSSA(sootMethod);
//                SootMethodUtil.updateLocalName(sootMethod);
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
        if (unit instanceof JIdentityStmt) {
            IdentityStmt stmt = (IdentityStmt) unit;
            return stmt.getRightOp() instanceof CaughtExceptionRef;
        }
        return false;
    }
//
//    public static Value getInvokeBase() {
//
//    }

}
