package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.entities.cfgpath.BaseCFGPath;
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

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:49
 */
public class PathAnalyzer {
    private static final Logger logger = Logger.getLogger(PathAnalyzer.class);
    private final List<CFGPath> paths;
    private final Set<SootMethod> meetMethods;

    public PathAnalyzer( Unit startUnit, List<CFGPath> paths,Set<SootMethod> meetMethods) {
        this.meetMethods = meetMethods;
        this.paths = paths;
        initialMeetMethod(startUnit);
    }

    public boolean analyze() {
        boolean res = false;
        for (CFGPath path : paths) {
            if (this.analyze(path)) {
                PathAnalyzer.reportStackUnitInfo(path);
                res = true;
                if (!OptionsArgs.outputAllLeakPaths) {
                    break;
                }
            }
        }
        return res;
    }

    private void initialMeetMethod(Unit unit) {
        SootMethod sootMethod = UnitUtil.getSootMethod(unit);
        this.meetMethods.add(sootMethod);
    }

    private static void updateLocalVariable(Unit curUnit, List<Unit> curPath, Set<Value> localVariables) {
        Value local = null;
        if (curUnit instanceof InvokeStmt) {
            if (ResourceUtil.isRequest(curUnit)) {
                if (localVariables.isEmpty()) {
                    local = UnitUtil.getInvokeBase(curUnit);
                } else {
                    InvokeStmt invokeStmt = (InvokeStmt) curUnit;
                    for (Value arg : invokeStmt.getInvokeExpr().getArgs()) {
                        if (localVariables.contains(arg)) {
                            logger.debug(UnitUtil.getInvokeBase(curUnit));
                        }
                    }
                }
            }
        } else if (curUnit instanceof DefinitionStmt) {
            if (UnitUtil.getDefineOp(curUnit, UnitUtil.rightOp) instanceof ParameterRef) {
                local = UnitUtil.getDefineOp(curUnit, UnitUtil.leftOp);
            } else {
                Value rightOp = UnitUtil.getDefineOp(curUnit, UnitUtil.rightOp);
                if (rightOp instanceof PhiExpr) {
                    rightOp = UnitUtil.getPhiValue(rightOp, curPath);
                }
                if (localVariables.contains(rightOp)) {
                    local = UnitUtil.getDefineOp(curUnit, UnitUtil.leftOp);
                }
            }
        }
        if (local != null) {
            localVariables.add(local);
        }
    }

    private boolean analyze(CFGPath cfgPath) {
        Set<Value> localVariables = new HashSet<>();
        List<Unit> path = cfgPath.getPath();
        if (path.isEmpty()) return false;
        updateLocalVariable(path.get(0), Collections.emptyList(), localVariables);
        for (int i = 1; i < path.size(); i++) {
            Unit curUnit = path.get(i);
            if (ResourceUtil.isRelease(curUnit, localVariables)) {
                return false;
            } else if (curUnit instanceof IfStmt) {
                if (i == path.size() - 1) {
                    logger.warn(String.format("IfStmt occurs in the last of a path: %s", path));
                    return true;
                }
                if (!branchReachable(localVariables, (IfStmt) curUnit, path.get(i + 1))) {
                    return false;
                }
            } else if (InterProcedureUtil.isInterProcedureCall(curUnit, localVariables)) {
                if (meetMethods.contains(InterProcedureUtil.getInvokeMethod(curUnit))) return true;
                if (!InterProcedureUtil.dealInterProcedureCall(curUnit, localVariables, new HashSet<>(meetMethods))) {
                    return false;
                }
            }
            updateLocalVariable(curUnit, path.subList(0, i), localVariables);
        }
        return true;
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
