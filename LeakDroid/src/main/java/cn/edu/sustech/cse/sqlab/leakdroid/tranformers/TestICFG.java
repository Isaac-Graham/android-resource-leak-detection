package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import org.apache.log4j.Logger;
import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.Collection;
import java.util.Map;

/**
 * @author Isniaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/18 22:19
 */
@PhaseName(name = "stp.testicfg")
public class TestICFG extends BodyTransformer {
    private final static Logger logger = Logger.getLogger(TestICFG.class);

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        ExceptionalUnitGraph cfg = new ExceptionalUnitGraph(body);
        body.getUnits().forEach(unit -> {
            if (unit instanceof InvokeStmt) {
                logger.info(((InvokeStmt) unit).getInvokeExpr().getMethodRef());
                logger.info(((InvokeStmt) unit).getInvokeExpr().getMethod());
            }
        });
//        try {
//            logger.info(String.format("Callee: %s, Caller: %s", ICFGDrawing.icfg., body.getMethod()));
//        } catch (RuntimeException e) {
//            logger.error(e.getMessage());
//        }
    }
}
