package cn.edu.sustech.cse.sqlab.leakdroid.util;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import soot.SootClass;
import soot.Unit;
import soot.jimple.InvokeStmt;

import java.io.FileInputStream;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:52
 */
public class ResourceUtil {
    // TODO: 完善该方法
    public static boolean isRequest(Unit unit) {
        if (ICFGContext.icfg.isCallStmt(unit)) {
            if (unit instanceof InvokeStmt) {
                InvokeStmt invokeStmt = (InvokeStmt) unit;
                if (invokeStmt.getInvokeExpr().getMethod().isConstructor()) {
                    return isFileInputStream(invokeStmt);
                }
            }
        }
        return false;
    }

    // TODO: 完善该方法
    public static boolean isRelease(Unit unit) {
        if (ICFGContext.icfg.isCallStmt(unit)) {
            if (unit instanceof InvokeStmt) {
                InvokeStmt invokeStmt = (InvokeStmt) unit;
                if (isFileInputStream(invokeStmt)) {
                    return invokeStmt.getInvokeExpr().getMethod().toString().contains("close");
                }
            }
        }
        return false;
    }

    public static boolean isFileInputStream(InvokeStmt invokeStmt) {
        SootClass sootClass = invokeStmt.getInvokeExpr().getMethod().getDeclaringClass();
        return sootClass.getName().equals(FileInputStream.class.getName());
    }
}
