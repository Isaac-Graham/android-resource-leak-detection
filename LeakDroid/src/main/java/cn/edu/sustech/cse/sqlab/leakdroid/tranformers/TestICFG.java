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
        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;
        SootMethodUtil.ensureSSA(body.getMethod());
        SootMethodUtil.updateLocalName(body.getMethod());


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


        body.getUnits().stream().filter(ResourceUtil::isRequest).forEach(ResourceLeakDetector::detect);
//        ResourceLeakDetector.detect(body.getUnits().getFirst());

//
//        Scene.v().getCallGraph().forEach(logger::info);

//        Unit startUnit = InterProcedureUtil.getStartUnit(body, 0);
//        PathExtractor.extractPath(startUnit);


    }
}
