package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.pathextractor.CFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.InterProcedureUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import org.apache.log4j.Logger;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.shimple.PhiExpr;

import java.util.*;

import static cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier.*;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:49
 */
public class PathAnalyzer {
    private static final Logger logger = Logger.getLogger(PathAnalyzer.class);
    private final Set<SootMethod> meetMethods;
    private final Set<Value> fieldValues;
    public boolean isEnd = false;

    public PathAnalyzer(SootMethod sootMethod, Set<SootMethod> meetMethods, Set<Value> fieldValues) {
        this.meetMethods = meetMethods;
        this.meetMethods.add(sootMethod);
        this.fieldValues = fieldValues;
    }

    public LeakIdentifier analyze(List<CFGPath> paths, boolean interProcedural) {
        LeakIdentifier res = NOT_LEAK;
        for (CFGPath path : paths) {
            LeakIdentifier analyzeRes = this.analyze(path);
            res = LeakIdentifier.max(res, analyzeRes);
            if (analyzeRes == LEAK) {
                if (interProcedural) {
                    break;
                }
                PathAnalyzer.reportStackUnitInfo(path);
                if (!OptionsArgs.outputAllLeakPaths) {
                    break;
                }
            }
        }
        return res;
    }

    private void updateLocalVariable(Unit curUnit, List<Unit> curPath, Set<Value> localVariables) {
        Value local = null;

        if (curUnit instanceof InvokeStmt && ResourceUtil.isRequest(curUnit)) {
            if (localVariables.isEmpty()) local = UnitUtil.getInvokeBase(curUnit);
        } else if (curUnit instanceof InvokeStmt && !ResourceUtil.isRequest(curUnit)) {
            InvokeStmt invokeStmt = (InvokeStmt) curUnit;
            for (Value arg : invokeStmt.getInvokeExpr().getArgs()) {
                if (localVariables.contains(arg)) {
                    local = UnitUtil.getInvokeBase(curUnit);
                }
            }
        } else if (curUnit instanceof DefinitionStmt && ResourceUtil.isRequest(curUnit)) {
            local = UnitUtil.getDefineOp(curUnit, UnitUtil.leftOp);
        } else if (curUnit instanceof DefinitionStmt && !ResourceUtil.isRequest(curUnit)) {
            Value rightOp = UnitUtil.getDefineOp(curUnit, UnitUtil.rightOp);
            if (rightOp instanceof ParameterRef) {
                local = UnitUtil.getDefineOp(curUnit, UnitUtil.leftOp);
            } else {
                if (rightOp instanceof PhiExpr) {
                    rightOp = UnitUtil.getPhiValue(rightOp, curPath);
                }
                if (localVariables.contains(rightOp)) {
                    local = UnitUtil.getDefineOp(curUnit, UnitUtil.leftOp);
                }
            }
        }
        if (local != null && !fieldValues.contains(local)) {
            localVariables.add(local);
        }
    }

    private LeakIdentifier analyze(CFGPath cfgPath) {
        Set<Value> localVariables = new HashSet<>();
        List<Unit> path = cfgPath.getPath();
        if (path.isEmpty()) return NOT_LEAK;
        updateLocalVariable(path.get(0), Collections.emptyList(), localVariables);
        if (localVariables.isEmpty()) return NOT_LEAK;

        for (int i = 1; i < path.size() && !isEnd; i++) {
            Unit curUnit = path.get(i);
            if (ResourceUtil.isRelease(curUnit, localVariables)) {
                return NOT_LEAK;
            } else if (curUnit instanceof IfStmt) {
                if (i == path.size() - 1) {
                    logger.warn(String.format("IfStmt occurs in the last of a path: %s", path));
                    return LEAK;
                }
                if (!branchReachable(localVariables, (IfStmt) curUnit, path.get(i + 1))) {
                    return NOT_LEAK;
                }
            } else if (InterProcedureUtil.isInterProcedureCall(curUnit, localVariables)) {
                if (meetMethods.contains(InterProcedureUtil.getInvokeMethod(curUnit))) return LEAK;
                if (InterProcedureUtil.dealInterProcedureCall(curUnit, localVariables, new HashSet<>(meetMethods)) == NOT_LEAK) {
                    return NOT_LEAK;
                }
            }
            updateLocalVariable(curUnit, path.subList(0, i), localVariables);
        }
        return isEnd ? UN_KNOWN : LEAK;
    }

    private static void reportStackUnitInfo(CFGPath cfgPath) {
        List<Unit> path = cfgPath.getPath();
        if (path.size() == 0) {
            return;
        }
        SootMethod leakMethod = UnitUtil.getSootMethod(path.get(0));
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            Unit leakUnit = path.get(i);
            if (!leakUnit.hasTag(ResourceLeakTag.name)) {
                leakUnit.addTag(new ResourceLeakTag());
            }
            if (i != path.size() - 1) {
                UnitUtil.getResourceLeakTag(leakUnit).addSuccessor(path.get(i + 1));
            }
            res.append(leakUnit).append(" -> ");
        }
        res.append("END");
        logger.info(String.format("Resource leak method: %s\n" +
                "\tResource leak path: %s", SootMethodUtil.getFullName(leakMethod), res));
    }

    private static boolean branchReachable(Set<Value> localValuables, IfStmt ifStmt, Unit nextUnit) {
        final String equal = "==";
        final String noEqual = "!=";
        ConditionExpr conditionExpr = (ConditionExpr) ifStmt.getCondition();

        Value op1 = conditionExpr.getOp1();
        String symbol = conditionExpr.getSymbol();
        Value op2 = conditionExpr.getOp2();
        if (!localValuables.contains(op1) && !localValuables.contains(op2)) {
            return true;
        }

        if (nextUnit == ifStmt.getTarget()) {
            if (localValuables.contains(op1) && localValuables.contains(op2)) {
                if (symbol.equals(equal)) {
                    return true;
                }
                logger.warn(String.format("Unknown condition: %s", conditionExpr));
            } else {
                Value noLocal = localValuables.contains(op1) ? op2 : op1;
                // != null
                if (symbol.equals(noEqual) && noLocal.equals(NullConstant.v())) {
                    return true;
                }
                logger.warn(String.format("Unknown condition: %s", conditionExpr));
            }
        } else {
            if (localValuables.contains(op1) && localValuables.contains(op2)) {
                if (symbol.equals(noEqual)) {
                    return true;
                }
                logger.warn(String.format("Unknown condition: %s", conditionExpr));
            } else {
                Value noLocal = localValuables.contains(op1) ? op2 : op1;
                // != null
                if (!symbol.equals(noEqual) && noLocal.equals(NullConstant.v())) {
                    return true;
                }
                logger.warn(String.format("Unknown condition: %s", conditionExpr));
            }
        }
        return false;
    }
}
