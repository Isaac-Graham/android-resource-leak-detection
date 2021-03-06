package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.AbstractSpecialInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

import java.io.FileInputStream;
import java.util.*;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/18 22:19
 */
@PhaseName(name = "stp.testicfg")
public class ICFGDetector extends BodyTransformer {
    private final static Logger logger = Logger.getLogger(ICFGDetector.class);

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        SootMethodUtil.ensureSSA(body.getMethod());
        SootMethodUtil.updateLocalName(body.getMethod());

        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;
        ExceptionalUnitGraph cfg = new ExceptionalUnitGraph(body);
        body.getUnits().stream().filter(Analyzer::isRequest).forEach(unit -> {
            if (Analyzer.isLeakage(unit, cfg)) {
                logger.info(body.getMethod());
            }
        });

        body.getUnits().stream().filter(Analyzer::isRequest).forEach(unit -> {
            logger.info("###");
            unit.getUseAndDefBoxes().forEach(logger::info);
//            AbstractSpecialInvokeExpr iie = (AbstractSpecialInvokeExpr) (((JInvokeStmt) unit).getInvokeExpr());
//            logger.info(iie.getBase());
        });
    }

}
