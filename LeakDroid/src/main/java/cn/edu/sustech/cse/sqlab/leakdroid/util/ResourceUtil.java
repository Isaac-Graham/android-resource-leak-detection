package cn.edu.sustech.cse.sqlab.leakdroid.util;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ResourceLeakDetector;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.UnitGraph;

import java.util.*;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:52
 */
public class ResourceUtil {
    private static final Logger logger = Logger.getLogger(ResourceUtil.class);
    private static final Set<String> javaResourcesType = new HashSet<String>() {{
        // IO Resources
        add(java.io.BufferedOutputStream.class.getName());
        add(java.io.BufferedReader.class.getName());
        add(java.io.BufferedWriter.class.getName());
        add(java.io.ByteArrayOutputStream.class.getName());
        add(java.io.DataOutputStream.class.getName());
        add(java.io.FileInputStream.class.getName());
        add(java.io.FileOutputStream.class.getName());
        add(java.io.FilterInputStream.class.getName());
        add(java.io.FilterOutputStream.class.getName());
        add(java.io.InputStream.class.getName());
        add(java.io.InputStreamReader.class.getName());
        add(java.io.ObjectInputStream.class.getName());
        add(java.io.ObjectOutputStream.class.getName());
        add(java.io.OutputStream.class.getName());
        add(java.io.OutputStreamWriter.class.getName());
        add(java.io.PipedOutputStream.class.getName());

        // NET Resources
        add(java.net.Socket.class.getName());

        // UTIL Resources
        add(java.util.Formatter.class.getName());
        add(java.util.Scanner.class.getName());
        add(java.util.concurrent.Semaphore.class.getName());
        add(java.util.logging.FileHandler.class.getName());

        // ANDROID Resources
        add("android.database.Cursor");
        add("android.net.wifi.WifiManager.WifiLock");
        add("android.os.PowerManager.WakeLock");
        add("android.database.sqlite.SQLiteDatabase");
        add("android.os.ParcelFileDescriptor");
    }};
    private static final HashMap<String, Set<SootMethod>> resourceRequestMethods = new HashMap<>();
    private static final Set<SootMethod> containsResourceMethods = new HashSet<>();

    // TODO: 完善该方法
    public static boolean isRequest(Unit unit) {
        InvokeExpr expr = null;
        if (unit instanceof InvokeStmt) {
            InvokeStmt invokeStmt = (InvokeStmt) unit;
            expr = invokeStmt.getInvokeExpr();
        } else if (unit instanceof DefinitionStmt) {
            Value rightOp = UnitUtil.getDefineOp(unit, UnitUtil.rightOp);
            if (!(rightOp instanceof InvokeExpr)) return false;
            expr = (InvokeExpr) rightOp;
        }
        return isValidRequestInvoke(expr)
                && getAllRequestMethods().contains(expr.getMethod());
    }

    private static boolean isValidRequestInvoke(InvokeExpr invokeExpr) {
        if (invokeExpr == null) return false;
        SootMethod sootMethod = invokeExpr.getMethod();
        return sootMethod.isStatic()
                || !sootMethod.getDeclaringClass().getName().contains("java.net")
                || !sootMethod.getReturnType().toQuotedString().contains("java.io");
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

    public static boolean isResource(Type type) {
        return javaResourcesType.contains(type.toQuotedString());
    }

    public static void addRequestMethod(Type resource, SootMethod sootMethod) {
//        UnitGraph cfg = ICFGContext.getCFGFromMethod(sootMethod);
//        cfg.getTails()
//        sootMethod.getActiveBody().getUnits();
        if (SootMethodUtil.isReturnedField(sootMethod)) return;
        if (!resourceRequestMethods.containsKey(resource.toQuotedString())) {
            resourceRequestMethods.put(resource.toQuotedString(), new HashSet<>());
        }
        resourceRequestMethods.get(resource.toQuotedString()).add(sootMethod);
    }

    public static Set<SootMethod> getAllRequestMethods() {
        Set<SootMethod> res = new HashSet<>();
        resourceRequestMethods.forEach((resourceType, requestMethod) -> {
            res.addAll(requestMethod);
        });
        return res;
    }

    public static Set<SootMethod> getMethodContainingResource() {
        return containsResourceMethods;
    }

    public static void addMethodContainingResource(SootMethod method) {
        containsResourceMethods.add(method);
    }
}
