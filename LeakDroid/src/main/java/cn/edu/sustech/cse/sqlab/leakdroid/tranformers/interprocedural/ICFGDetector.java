package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.PathExtractor;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.CFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
        if (!body.getMethod().getName().contains("singleWhileLoopTest")) return;
        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;
        SootMethodUtil.ensureSSA(body.getMethod());
        SootMethodUtil.updateLocalName(body.getMethod());
//        new LoopFinder().getLoops(body).forEach(loop -> {
//            List<Stmt> lss = loop.getLoopStatements();
//            logger.info(lss);
////            loop.getLoopExits().forEach(loopExit -> {
////                logger.info()
////            });
//        });

//        body.getUnits().stream().filter(Analyzer::isRequest).forEach(unit -> {
//            if (new Analyzer(body).isLeakage((InvokeStmt) unit)) {
//                logger.info(body.getMethod());
//            }
//        });

//        ICFGContext.cfgGraphs.put(body, new ExceptionalUnitGraph(body));
//        List<Unit> units = new ArrayList<>(body.getUnits());
//        Set<CFGPath> paths = PathExtractor.extractPath(units.get(0));
//        paths.forEach(logger::info);
        new B().funcA();
//
//
//        Test test = new Test();
//        for (int i = 0; i < 3; i++) {
//            test.add(units.get(i));
//        }
//        test.add(null);
//        Test test02 = (Test) test.clone();
//        test.printStackInfo();
//        test02.printStackInfo();
//        test.add(units.get(4));
//        logger.info("###");
//        test.printStackInfo();
//        test02.printStackInfo();

    }

    class A {
        protected void funcA() {
            this.funcB();
        }

        protected void funcB() {
            logger.info("Func B in Class A");
        }
    }

    class B extends A {
        protected void funcB() {
            logger.info("Func B in Class B");
        }

        protected void funcA() {
            super.funcA();
        }
    }
}
