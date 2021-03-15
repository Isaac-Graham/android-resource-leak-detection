package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ResourceLeakDetector;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer.PathAnalyzer;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer.PathExtractor;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.BaseCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.InterProcedureUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;
import soot.jimple.internal.AbstractDefinitionStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.*;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/18 22:19
 */
@PhaseName(name = "stp.testicfg")
public class TestICFG extends BodyTransformer {
    private final static Logger logger = Logger.getLogger(TestICFG.class);


    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
//        Scene.v().getApplicationClasses().forEach(sootClass -> {
//            sootClass.getMethods().forEach(method -> {
//                logger.info(method.getName());
//            });
//        });
        if (!SootMethodUtil.getFullName(body.getMethod()).contains("cn.edu.sustech.cse.sqlab.testSoot.MainActivity.testClose")
                && !SootMethodUtil.getFullName(body.getMethod()).contains("cn.edu.sustech.cse.sqlab.testSoot.MainActivity.close"))
            return;
        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;
        SootMethod method = Scene.v().getMethod("<cn.edu.sustech.cse.sqlab.testSoot.MainActivity: void close(java.io.Closeable)>");

        logger.info(SootMethodUtil.getFullName(body.getMethod()));
        logger.info(String.format("method: %d", System.identityHashCode(body.getMethod())));
        logger.info(String.format("body: %d", System.identityHashCode(body.getMethod().getActiveBody())));
        logger.info(String.format("body: %d", System.identityHashCode(body)));
        logger.info(String.format("unit: %d", System.identityHashCode(body.getUnits().getFirst())));
        logger.info(String.format("icfg.getMethod(): %d", System.identityHashCode(ICFGContext.icfg.getMethodOf(body.getUnits().getFirst()))));
        logger.info(String.format("icfg.getBody(): %d", System.identityHashCode(ICFGContext.icfg.getBodyOf(body.getUnits().getFirst()))));
        logger.info(SootMethodUtil.getFullName(method));
        logger.info(String.format("close method: %d", System.identityHashCode(method)));
        logger.info(String.format("close body: %d", System.identityHashCode(method.getActiveBody())));
        logger.info(String.format("close body: %d", System.identityHashCode(body)));
        logger.info(String.format("close unit: %d", System.identityHashCode(body.getUnits().getFirst())));
        logger.info(String.format("close icfg.getMethod(): %d", System.identityHashCode(ICFGContext.icfg.getMethodOf(body.getUnits().getFirst()))));
        logger.info(String.format("close icfg.getBody(): %d", System.identityHashCode(ICFGContext.icfg.getBodyOf(body.getUnits().getFirst()))));
//        SootMethodUtil.ensureSSA(body.getMethod());
//        SootMethodUtil.updateLocalName(body.getMethod());


//        ICFGContext.addCFGFromBody(body);
//        ICFGContext.addLoopFromBody(body);
//        body.getUnits().forEach(unit -> {
//            if (unit.toString().contains("cn.edu.sustech.cse.sqlab.testSoot.MainActivity.close.l0 := @parameter0: java.io.Closeable")) {
//                logger.info(ICFGContext.icfg.getBodyOf(unit));
//            }
//        });

//        body.getUnits().forEach(unit -> {
//            if (unit instanceof JIdentityStmt) {
//                AbstractDefinitionStmt stmt = (AbstractDefinitionStmt) unit;
//                logger.info(unit);
//                logger.info(stmt.getRightOp().getClass());
//            }
//        });


//        body.getUnits().forEach(unit -> {
//            if (unit instanceof InvokeStmt) {
//                logger.info("###");
//                logger.info(unit);
//                InvokeStmt invokeStmt = (InvokeStmt) unit;
//                logger.info(invokeStmt.getInvokeExpr().getArgs());
//            }
//        });


        body.getUnits().stream().filter(ResourceUtil::isRequest).forEach(unit -> {
            ResourceLeakDetector.detect(body.getMethod(), unit);
        });
        ResourceLeakDetector.detect(body.getMethod(), body.getUnits().getFirst());

//
//        Scene.v().getCallGraph().forEach(logger::info);

//        Unit startUnit = InterProcedureUtil.getStartUnit(body, 0);
//        PathExtractor.extractPath(startUnit);


    }
}
