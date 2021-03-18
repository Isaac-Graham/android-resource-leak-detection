package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.BaseCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.InterProcedureUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import org.apache.log4j.Logger;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.jimple.internal.AbstractDefinitionStmt;
import soot.jimple.internal.AbstractSpecialInvokeExpr;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;

import java.util.*;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:49
 */
public class PathAnalyzer {
    private static final Logger logger = Logger.getLogger(PathAnalyzer.class);
    private final HashMap<Value, List<Unit>> localDefHashMap;
    private final List<BaseCFGPath> paths;
    private final Set<SootMethod> meetMethods;

    public PathAnalyzer(List<BaseCFGPath> paths, Unit startUnit, Set<SootMethod> meetMethods) {
        this.localDefHashMap = new HashMap<>();
        this.meetMethods = meetMethods;
        this.paths = paths;
        initialLocalDefHashMap(startUnit);
        initialMeetMethod(startUnit);
    }

    public boolean analyze() {
        boolean res = false;
        for (BaseCFGPath path : paths) {
            if (this.analyze(path)) {
                PathAnalyzer.reportStackUnitInfo(path);
                res = true;
            }
        }
        return res;
    }

    private void initialLocalDefHashMap(Unit startUnit) {
        SootMethod sootMethod = UnitUtil.getSootMethod(startUnit);
        ExceptionalUnitGraph cfg = ICFGContext.getCFGFromMethod(sootMethod);
        SimpleLocalDefs sld = new SimpleLocalDefs(cfg);
        if (sootMethod == null) {
            return;
        }
        sootMethod.getActiveBody().getLocals().forEach(local -> {
            if (sld.getDefsOf(local).stream().anyMatch(unit -> !(unit instanceof DefinitionStmt))) {
                logger.error(String.format("Error occurs: All should be DefinitionStmt in def list: %s", sld.getDefsOf(local)));
            }
            this.localDefHashMap.put(local, sld.getDefsOf(local));
        });
    }

    private void initialMeetMethod(Unit unit) {
        SootMethod sootMethod = UnitUtil.getSootMethod(unit);
        this.meetMethods.add(sootMethod);
    }

    private static Value getFirstVariable(Unit unit) {
        if (unit instanceof InvokeStmt) {
            InvokeStmt invokeStmt = (InvokeStmt) unit;
            AbstractSpecialInvokeExpr abstractStartSpecialInvokeExpr = (AbstractSpecialInvokeExpr) invokeStmt.getInvokeExpr();
            return abstractStartSpecialInvokeExpr.getBase();
        } else if (unit instanceof AbstractDefinitionStmt) {
            AbstractDefinitionStmt stmt = (AbstractDefinitionStmt) unit;
            if (stmt.getRightOp() instanceof ParameterRef) {
                return stmt.getLeftOp();
            }
        }
        return null;
    }

    private boolean analyze(BaseCFGPath cfgPath) {
        Set<Value> localValuables = new HashSet<>();
        List<Unit> path = cfgPath.getPath();
        if (path.isEmpty()) return false;
        localValuables.add(getFirstVariable(path.get(0)));
        for (int i = 1; i < path.size(); i++) {
            Unit nextUnit = path.get(i);
            if (ResourceUtil.isRelease(nextUnit, localValuables)) {
                return false;
            } else if (nextUnit instanceof IfStmt) {
                if (i == path.size() - 1) {
                    logger.warn(String.format("IfStmt occurs in the last of a path: %s", path));
                    return true;
                }
                if (!branchReachable(localValuables, (IfStmt) nextUnit, path.get(i + 1))) {
                    return false;
                }
            } else if (InterProcedureUtil.isInterProcedureCall(nextUnit, localValuables)) {
                if (meetMethods.contains(InterProcedureUtil.getInvokeMethod(nextUnit))) return true;
                if (!InterProcedureUtil.dealInterProcedureCall(nextUnit, localValuables, new HashSet<>(meetMethods))) {
                    return false;
                }
            }
            Value localValueThisUnit = getLocalValueFromDefinitions(nextUnit, localValuables);
            if (localValueThisUnit != null) {
                localValuables.add(localValueThisUnit);
            }
        }
        return true;
    }

    private Value getLocalValueFromDefinitions(Unit nextUnit, Set<Value> localValuables) {
        Set<Map.Entry<Value, List<Unit>>> entrySet = this.localDefHashMap.entrySet();
        for (Map.Entry<Value, List<Unit>> valueListEntry : entrySet) {
            for (Unit def : valueListEntry.getValue()) {
                if (def == nextUnit) {
                    Value rightOp = ((DefinitionStmt) def).getRightOp();
                    if (localValuables.contains(rightOp)) {
                        return ((DefinitionStmt) def).getLeftOp();
                    }
                }
            }
        }
        return null;
    }

    private static void reportStackUnitInfo(BaseCFGPath cfgPath) {
        List<Unit> path = cfgPath.getPath();
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
        logger.info(String.format("Resource leak path: %s", res));
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
