package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import org.apache.log4j.Logger;
import soot.SootClass;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.toolkits.graph.UnitGraph;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/4 14:25
 */
public class Analyzer {
    private static final Logger logger = Logger.getLogger(Analyzer.class);

    /**
     * @param startUnit The start node of DFS should be the request node;
     * @return TRUE if there is such a path that release statement could not be reached.
     * FALSE if release statement could be reached in each paths
     */
    public static boolean isLeakage(Unit startUnit, UnitGraph cfg) {
        Stack<Unit> mainStack = new Stack<>();
        Stack<Stack<Unit>> assistStack = new Stack<>();
        List<String> variableNames = new ArrayList<>();
        mainStack.add(startUnit);
        assistStack.add(new Stack<>());
        cfg.getSuccsOf(startUnit).forEach(successor -> {
            assistStack.peek().add(successor);
        });
        variableNames.add(getInvokeCaller((InvokeStmt) startUnit));

        while (!mainStack.empty()) {
            Unit currentUnit = mainStack.peek();
            if (ICFGContext.icfg.isExitStmt(currentUnit)) {
                reportStackUnitInfo(mainStack);
                return true;
            } else if (isRelease(currentUnit)) {
                dfsCallBack(mainStack, assistStack, variableNames);
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

                variableNames.add(nextVariableName);
                mainStack.push(nextUnit);
                assistStack.add(new Stack<>());

                cfg.getSuccsOf(nextUnit).forEach(successor -> {
                    assistStack.peek().add(successor);
                });
            } else {
                dfsCallBack(mainStack, assistStack, variableNames);
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

    private static void reportStackUnitInfo(Stack<Unit> mainStack) {
        StringBuilder res = new StringBuilder();
        mainStack.forEach(ele -> {
            ele.addTag(new ResourceLeakTag());
            res.append(ele).append(" -> ");
        });
        res.append("END");
//        logger.info(String.format("Resource leak path: %s", res));
    }

    private static String getInvokeCaller(InvokeStmt invokeStmt) {
        String stmt = invokeStmt.toString();
        return stmt.substring(stmt.indexOf(' '), stmt.indexOf('.'));
    }

    private static void dfsCallBack(Stack<Unit> mainStack, Stack<Stack<Unit>> assistStack, List<String> variableNames) {
        mainStack.pop();
        assistStack.pop();
        variableNames.remove(variableNames.size() - 1);
    }

}
