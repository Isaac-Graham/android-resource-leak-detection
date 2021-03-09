package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import soot.Body;
import soot.SootClass;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.internal.AbstractSpecialInvokeExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;

import java.io.FileInputStream;
import java.util.*;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/4 14:25
 */
public class Analyzer {
    private static final Logger logger = Logger.getLogger(Analyzer.class);
    private final Stack<Unit> mainStack;
    private final Stack<Stack<Unit>> assistStack;
    private final List<Value> localValuables;
    private final UnitGraph cfg;
    private final HashMap<Value, List<Unit>> localDefHashMap;

    public Analyzer(Body body) {
        this.mainStack = new Stack<>();
        this.assistStack = new Stack<>();
        this.localValuables = new ArrayList<>();
        this.cfg = new ExceptionalUnitGraph(body);
        this.localDefHashMap = new HashMap<>();
        initialLocalDefHashMap(body);
    }


    private void initial(InvokeStmt startUnit) {
        mainStack.add(startUnit);
        assistStack.add(new Stack<>());
        cfg.getSuccsOf(startUnit).forEach(successor -> {
            assistStack.peek().add(successor);
        });
        localValuables.add(getFirstVariable(startUnit));
    }

    private void initialLocalDefHashMap(Body body) {
        SimpleLocalDefs sld = new SimpleLocalDefs(this.cfg);
        SimpleLocalUses slu = new SimpleLocalUses(body, sld);
        body.getLocals().forEach(local -> {
            if (sld.getDefsOf(local).stream().anyMatch(unit -> !(unit instanceof DefinitionStmt))) {
                logger.error(String.format("Error occurs: All should be DefinitionStmt in def list: %s", sld.getDefsOf(local)));
            }
            this.localDefHashMap.put(local, sld.getDefsOf(local));
        });
    }

    private void addNewPathNode(Value newVariable, Unit nextUnit) {
        localValuables.add(newVariable);
        mainStack.push(nextUnit);
        assistStack.add(new Stack<>());
        cfg.getSuccsOf(nextUnit).forEach(successor -> {
            assistStack.peek().add(successor);
        });
    }

    /**
     * @param startUnit The start node of DFS should be the request node;
     * @return TRUE if there is such a path that release statement could not be reached.
     * FALSE if release statement could be reached in each paths
     */
    public boolean isLeakage(InvokeStmt startUnit) {
        initial(startUnit);
        while (!mainStack.empty()) {
            Unit currentUnit = mainStack.peek();
            if (ICFGContext.icfg.isExitStmt(currentUnit)) {
                reportStackUnitInfo();
//                return true;
            } else if (isRelease(currentUnit)) {
                dfsCallBack();
                continue;
            }
            Stack<Unit> assistStackTop = assistStack.peek();
            if (!assistStackTop.empty()) {
                Unit nextUnit = assistStackTop.pop();
                Value localValueThisUnit = getLocalValueFromDefinitions(nextUnit);
                if (nextUnit instanceof InvokeStmt) {
                    logger.info(nextUnit.getClass());
                    logger.info(nextUnit);
                }
                addNewPathNode(localValueThisUnit, nextUnit);

            } else {
                dfsCallBack();
            }
        }
        return false;
    }

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

    private void reportStackUnitInfo() {
        StringBuilder res = new StringBuilder();
        mainStack.forEach(ele -> {
            ele.addTag(new ResourceLeakTag());
            res.append(ele).append(" -> ");
        });
        res.append("END");
//        logger.info(String.format("Resource leak path: %s", res));
    }

    private void debugPrintMainStack() {
        if (ICFGContext.icfg.isExitStmt(mainStack.peek()))
            logger.debug(String.format("Path: %s", Lists.newArrayList(mainStack)));
    }

    private void dfsCallBack() {
        debugPrintMainStack();
        mainStack.pop();
        assistStack.pop();
        localValuables.remove(localValuables.size() - 1);
    }

    private static Value getFirstVariable(InvokeStmt startInvokeStmt) {
        AbstractSpecialInvokeExpr abstractStartSpecialInvokeExpr = (AbstractSpecialInvokeExpr) startInvokeStmt.getInvokeExpr();
        return abstractStartSpecialInvokeExpr.getBase();
    }

    private Value getLocalValueFromDefinitions(Unit nextUnit) {
        Set<Map.Entry<Value, List<Unit>>> entrySet = this.localDefHashMap.entrySet();
        for (Map.Entry<Value, List<Unit>> valueListEntry : entrySet) {
            for (Unit def : valueListEntry.getValue()) {
                if (def == nextUnit) {
                    return ((DefinitionStmt) def).getLeftOp();
                }
            }
        }
        return null;
    }


}
