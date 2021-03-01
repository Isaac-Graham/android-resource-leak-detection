package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import org.apache.log4j.Logger;
import soot.Body;
import soot.BodyTransformer;
import soot.SootClass;
import soot.Unit;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

import java.io.FileInputStream;
import java.util.*;

/**
 * @author Isniaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/18 22:19
 */
@PhaseName(name = "stp.testicfg")
public class ICFGDetector extends BodyTransformer {
    private final static Logger logger = Logger.getLogger(ICFGDetector.class);

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        ExceptionalUnitGraph cfg = new ExceptionalUnitGraph(body);
        body.getUnits().forEach(unit -> {
            if (isRequest(unit)) {
                if (!DFS(unit, cfg)) {
                    logger.info(body.getMethod());
                }
            }
        });
    }


    private static boolean isRequest(Unit unit) {
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

    private static boolean isRelease(Unit unit) {
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

    private static boolean isFileInputStream(InvokeStmt invokeStmt) {
        SootClass sootClass = invokeStmt.getInvokeExpr().getMethod().getDeclaringClass();
        return sootClass.getName().equals(FileInputStream.class.getName());
    }

    /**
     * @param start The start node of DFS should be the request node;
     * @return true if release statement could be reached in each paths
     * false if there is such a path that release statement could not be reached.
     */
    private static boolean DFS(Unit start, UnitGraph cfg) {
        Stack<Unit> mainStack = new Stack<>();
        Stack<Stack<Unit>> assistStack = new Stack<>();
        mainStack.add(start);
        assistStack.add(new Stack<>());
        cfg.getSuccsOf(start).forEach(successor -> {
            assistStack.peek().add(successor);
        });

        while (!mainStack.empty()) {
            Unit currentUnit = mainStack.peek();
            if (ICFGContext.icfg.isExitStmt(currentUnit)) {
                StringBuilder res = new StringBuilder();
                mainStack.forEach(ele -> {
                    ele.addTag(new ResourceLeakTag());
                    res.append(ele).append(" -> ");
                });
                res.append("END");
                logger.info(String.format("Resource leak path: %s", res));
                return false;
            } else if (isRelease(currentUnit)) {
                mainStack.pop();
                assistStack.pop();
                continue;
            }
            Stack<Unit> assistStackTop = assistStack.peek();
            if (!assistStackTop.empty()) {
                Unit nextUnit = assistStackTop.pop();
                mainStack.push(nextUnit);
                assistStack.add(new Stack<>());
                cfg.getSuccsOf(nextUnit).forEach(successor -> {
                    assistStack.peek().add(successor);
                });
            } else {
                mainStack.pop();
                assistStack.pop();
            }
        }
        return true;
    }

}
