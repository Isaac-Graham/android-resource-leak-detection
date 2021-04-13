package cn.edu.sustech.cse.sqlab.leakdroid.util;

import org.apache.log4j.Logger;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/4 16:50
 */
public class SootMethodUtil {
    private static final Logger logger = Logger.getLogger(SootMethodUtil.class);

    public static String getFullName(SootMethod sootMethod) {
        return String.format("%s.%s", SootClassUtil.getFullName(sootMethod.getDeclaringClass()), sootMethod.getName());
    }

    public static String getFileNameString(SootMethod sootMethod) {
        List<RefType> parameterRef = sootMethod.getParameterTypes()
                .stream()
                .map(type -> (RefType) type)
                .collect(Collectors.toList());

        String parameterTypesString = parameterRef
                .stream()
                .map(type -> SootClassUtil.getShortName(type.getSootClass()))
                .collect(Collectors.joining(","));

        String fileName = String.format("%s_%s_%s(%s).dot",
                SootClassUtil.getShortName(sootMethod.getDeclaringClass()),
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
