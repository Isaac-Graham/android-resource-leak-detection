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
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.shimple.PhiExpr;
import soot.shimple.internal.SPhiExpr;
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
//        if (!SootMethodUtil.getFullName(body.getMethod()).contains("foo"))
//            return;
        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;

        body.getUnits().stream().filter(ResourceUtil::isRequest).forEach(unit -> {
            new ResourceLeakDetector(unit).detect();
        });
//        Unit startUnit = body.getUnits().getFirst();
//
//        body.getUnits().forEach(unit -> {
//            if (unit instanceof DefinitionStmt) {
//                Value rightOp = UnitUtil.getDefineOp(unit, UnitUtil.rightOp);
//                if (rightOp instanceof PhiExpr) {
//                    logger.debug("###");
//                    logger.debug(rightOp);
//                    PhiExpr phiExpr = (PhiExpr) rightOp;
//                    logger.debug(phiExpr.getArgs());
////                    phiExpr.getArgs().forEach(arg -> {
////                        logger.info(arg.getUnit());
////                    });
//                }
//            }
//        });
    }
}
