package cn.edu.sustech.cse.sqlab.leakdroid.util;

import soot.SootMethod;
import soot.Type;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Type> parameterTypes = sootMethod.getParameterTypes();
        String parameterTypesString = parameterTypes.stream().map(Type::toString).collect(Collectors.joining(","));

        String fileName = String.format("%s_%s_%s(%s).dot",
                SootMethodUtil.getClassName(sootMethod),
                sootMethod.getReturnType(),
                sootMethod.getName(),
                parameterTypesString);
        return fileName
                .replace('<', 'l')
                .replace('>', 'r');
    }

    public static String getFolderName(SootMethod sootMethod) {
        return sootMethod.getDeclaringClass().getPackageName();
    }

    public static void ensureSSA(SootMethod sootMethod) {
        if (!sootMethod.isConcrete()) return;
        if (!(sootMethod.retrieveActiveBody() instanceof ShimpleBody)) {
            sootMethod.setActiveBody(Shimple.v().newBody(sootMethod.retrieveActiveBody()));
        }
    }

    public static void updateLocalName(SootMethod sootMethod) {
        if (!sootMethod.isConcrete()) return;
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
