package cn.edu.sustech.cse.sqlab.leakdroid.util;

import soot.SootMethod;
import soot.Type;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;

import java.util.Arrays;
import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/4 16:50
 */
public class SootMethodUtil {

    public static String getFullName(SootMethod sootMethod) {
        return String.format("%s.%s", SootMethodUtil.getClassName(sootMethod), sootMethod.getName());
    }

    public static String getClassName(SootMethod sootMethod) {
        return sootMethod.getDeclaringClass().getName();
    }

    public static String getFileNameString(SootMethod sootMethod) {
        Type[] parameterTypes = sootMethod.getParameterTypes().toArray(new Type[]{});
        String[] parameterTypesString = Arrays.copyOf(parameterTypes, parameterTypes.length, String[].class);
        return String.format("%s_%s_%s(%s)",
                SootMethodUtil.getClassName(sootMethod),
                sootMethod.getReturnType(),
                sootMethod.getName(), String.join(",", parameterTypesString));
    }

    public static String getFolderName(SootMethod sootMethod) {
        return sootMethod.getDeclaringClass().getPackageName();
    }

    public static void ensureSSA(SootMethod sootMethod) {
        // TODO: require(this.isConcrete)
        if (!(sootMethod.retrieveActiveBody() instanceof ShimpleBody)) {
            sootMethod.setActiveBody(Shimple.v().newBody(sootMethod.retrieveActiveBody()));
        }
    }

    public static void updateLocalName(SootMethod sootMethod) {
        // TODO: require(this.isConcrete)
        if (sootMethod.retrieveActiveBody()
                .getLocals()
                .stream()
                .anyMatch(local -> local.getName().contains(sootMethod.getName()))) {
            return;
        }
        sootMethod.retrieveActiveBody().getLocals().forEach(local -> {
            local.setName(String.format("%s.%s", SootMethodUtil.getFullName(sootMethod), local.getName()));
        });
    }
}
