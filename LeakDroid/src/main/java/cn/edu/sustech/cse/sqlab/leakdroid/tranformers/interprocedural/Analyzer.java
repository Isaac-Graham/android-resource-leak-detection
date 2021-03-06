package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import soot.SootClass;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.internal.AbstractSpecialInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.toolkits.graph.UnitGraph;

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
    private final List<String> variableNames;
    private final UnitGraph cfg;
    private final Set<Value> localValues;

    public Analyzer(UnitGraph cfg) {
        this.mainStack = new Stack<>();
        this.assistStack = new Stack<>();
        this.variableNames = new ArrayList<>();
        this.cfg = cfg;
        this.localValues = new HashSet<>();
    }

    private void initial(InvokeStmt startUnit) {
        mainStack.add(startUnit);
        assistStack.add(new Stack<>());
        cfg.getSuccsOf(startUnit).forEach(successor -> {
            assistStack.peek().add(successor);
        });
        variableNames.add(getInvokeCaller(startUnit));
        localValues.add(getFirstVariable(startUnit));
    }

    private void addNewPathNode(String nextVariableName, Unit nextUnit) {
        variableNames.add(nextVariableName);
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
                String nextVariableName = "";
                if (nextUnit instanceof AssignStmt) {
                    AssignStmt assignStmt = (AssignStmt) nextUnit;
                    if (variableNames.contains(assignStmt.getRightOp().toString())) {
                        nextVariableName = assignStmt.getLeftOp().toString();
                    }
                }
                addNewPathNode(nextVariableName, nextUnit);

            } else {
                dfsCallBack();
            }
        }
        return false;
    }

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

    private static String getInvokeCaller(InvokeStmt invokeStmt) {
        String stmt = invokeStmt.toString();
        return stmt.substring(stmt.indexOf(' '), stmt.indexOf('.'));
    }

    private void dfsCallBack() {
        debugPrintMainStack();
        mainStack.pop();
        assistStack.pop();
        variableNames.remove(variableNames.size() - 1);
    }

    private static Value getFirstVariable(InvokeStmt startInvokeStmt) {
        AbstractSpecialInvokeExpr abstractStartSpecialInvokeExpr = (AbstractSpecialInvokeExpr) startInvokeStmt.getInvokeExpr();
        return abstractStartSpecialInvokeExpr.getBase();
    }

}
