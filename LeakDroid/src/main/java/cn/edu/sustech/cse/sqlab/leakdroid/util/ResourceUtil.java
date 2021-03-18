package cn.edu.sustech.cse.sqlab.leakdroid.util;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import org.apache.log4j.Logger;
import soot.SootClass;
import soot.Unit;
import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.internal.JInvokeStmt;

import java.io.FileInputStream;
import java.util.Set;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:52
 */
public class ResourceUtil {
    private static final Logger logger = Logger.getLogger(ResourceUtil.class);

    // TODO: 完善该方法
    public static boolean isRequest(Unit unit) {
        if (unit instanceof InvokeStmt) {
            InvokeStmt invokeStmt = (InvokeStmt) unit;
            if (invokeStmt.getInvokeExpr().getMethod().isConstructor()) {
                return isFileInputStream(invokeStmt);
            }
        }
        return false;
    }

    // TODO: 完善该方法
    public static boolean isRelease(Unit unit, Set<Value> locals) {
        if (unit instanceof InvokeStmt) {
            InvokeExpr invokeExpr = ((InvokeStmt) unit).getInvokeExpr();
            if (invokeExpr instanceof InstanceInvokeExpr) {
                InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
                return locals.contains(instanceInvokeExpr.getBase())
                        && instanceInvokeExpr.getMethod().toString().contains("close");
            }
        }
        return false;
    }

    public static boolean isFileInputStream(InvokeStmt invokeStmt) {
        SootClass sootClass = invokeStmt.getInvokeExpr().getMethod().getDeclaringClass();
        return sootClass.getName().equals(FileInputStream.class.getName());
    }
}
