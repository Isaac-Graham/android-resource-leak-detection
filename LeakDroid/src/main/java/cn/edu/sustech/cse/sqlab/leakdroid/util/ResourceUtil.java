package cn.edu.sustech.cse.sqlab.leakdroid.util;

import org.apache.log4j.Logger;
import soot.SootClass;
import soot.Unit;
import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:52
 */
public class ResourceUtil {
    private static final Logger logger = Logger.getLogger(ResourceUtil.class);
    private static final List<String> javaResourcesType = Arrays.asList(
            // IO Resources
            java.io.BufferedOutputStream.class.getName(),
            java.io.BufferedReader.class.getName(),
            java.io.BufferedWriter.class.getName(),
            java.io.ByteArrayOutputStream.class.getName(),
            java.io.DataOutputStream.class.getName(),
            java.io.FileInputStream.class.getName(),
            java.io.FileOutputStream.class.getName(),
            java.io.FilterInputStream.class.getName(),
            java.io.FilterOutputStream.class.getName(),
            java.io.InputStream.class.getName(),
            java.io.InputStreamReader.class.getName(),

            java.io.ObjectInputStream.class.getName(),
            java.io.ObjectOutputStream.class.getName(),
            java.io.OutputStream.class.getName(),
            java.io.OutputStreamWriter.class.getName(),
            java.io.PipedOutputStream.class.getName(),

            // NET Resources
            java.net.Socket.class.getName(),

            // UTIL Resources
            java.util.Formatter.class.getName(),
            java.util.Scanner.class.getName(),
            java.util.concurrent.Semaphore.class.getName(),
            java.util.logging.FileHandler.class.getName()
    );

    // TODO: 完善该方法
    public static boolean isRequest(Unit unit) {
        if (unit instanceof InvokeStmt) {
            InvokeStmt invokeStmt = (InvokeStmt) unit;
            if (invokeStmt.getInvokeExpr().getMethod().isConstructor()) {
                return isJavaResource(invokeStmt);
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

    public static boolean isJavaResource(InvokeStmt invokeStmt) {
        SootClass sootClass = invokeStmt.getInvokeExpr().getMethod().getDeclaringClass();
        return javaResourcesType.stream().anyMatch(resourceType -> resourceType.equals(sootClass.getName()));
    }
}
