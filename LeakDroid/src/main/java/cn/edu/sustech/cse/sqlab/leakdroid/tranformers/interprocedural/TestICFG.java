package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import org.apache.log4j.Logger;
import soot.Body;
import soot.BodyTransformer;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;

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
        logger.info(body.getMethod());
        ExceptionalUnitGraph cfg = new ExceptionalUnitGraph(body);
        body.getUnits().forEach(unit -> {
//            if (ICFGContext.icfg.isCallStmt(unit)) {
//                logger.info(unit);
//            }
//            if (unit instanceof InvokeStmt) {
//                Body anotherBody = ICFGContext.icfg.getBodyOf(unit);
////                Body anotherBody = ICFGContext.methodBodyMap.get(((InvokeStmt) unit).getInvokeExpr().getMethod());
//                if (anotherBody == null) {
//                    logger.info(String.format("Body of %s not found", ((InvokeStmt) unit).getInvokeExpr().getMethod()));
//                } else {
//                    logger.debug(anotherBody);
//                }
//            }
        });
//        try {
//            logger.info(String.format("Callee: %s, Caller: %s", ICFGDrawing.icfg., body.getMethod()));
//        } catch (RuntimeException e) {
//            logger.error(e.getMessage());
//        }
    }
}
