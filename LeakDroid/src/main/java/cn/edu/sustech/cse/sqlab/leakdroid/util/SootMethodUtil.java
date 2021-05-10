package cn.edu.sustech.cse.sqlab.leakdroid.util;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import org.apache.log4j.Logger;
import polyglot.ast.Return;
import soot.*;
import soot.jimple.DefinitionStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ThisRef;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.toolkits.graph.UnitGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        String parameterTypesString = sootMethod.getParameterTypes()
                .stream()
                .map(type -> {
                    if (type instanceof RefType) {
                        return SootClassUtil.getShortName(((RefType) type).getSootClass());
                    } else {
                        return type.toString();
                    }
                })
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

    public static Set<Value> getFieldValue(SootMethod sootMethod) {
        /**
         * TODO: This method cannot collect all of the field values in theory
         * The result depends on the scanning order of units
         */
        Set<Value> fieldValues = new HashSet<>();
        if (!sootMethod.hasActiveBody()) {
            return fieldValues;
        }
        Body body = sootMethod.getActiveBody();
        body.getUnits()
                .stream()
                .filter(unit -> unit instanceof DefinitionStmt)
                .forEach(unit -> {
                    Value rightOp = UnitUtil.getDefineOp(unit, UnitUtil.rightOp);
                    if (rightOp instanceof ThisRef || fieldValues.contains(rightOp)) {
                        fieldValues.add(UnitUtil.getDefineOp(unit, UnitUtil.leftOp));
                    }
                });
        return fieldValues;
    }

    public static boolean isReturnedField(SootMethod sootMethod) {
        if (!sootMethod.hasActiveBody()) return false;
        List<ReturnStmt> returnStmts = sootMethod.getActiveBody()
                .getUnits()
                .parallelStream()
                .filter(tail -> tail instanceof ReturnStmt)
                .map(tail -> (ReturnStmt) tail)
                .collect(Collectors.toList());
        if (returnStmts.isEmpty()) return false;
        Set<Value> fieldValues = getFieldValue(sootMethod);
        return returnStmts.parallelStream().anyMatch(tail -> fieldValues.contains(tail.getOp()));
    }
}
