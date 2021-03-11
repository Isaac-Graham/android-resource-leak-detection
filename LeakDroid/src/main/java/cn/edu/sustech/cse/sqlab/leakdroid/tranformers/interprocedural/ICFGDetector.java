package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.pathutils.LoopExitPathUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.pathutils.LoopOncePathUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.pathutils.LoopPathUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.ExceptionalUnitGraph;

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
        if (!body.getMethod().getName().contains("nestedLoopTest")) return;
        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;
        SootMethodUtil.ensureSSA(body.getMethod());
        SootMethodUtil.updateLocalName(body.getMethod());

        ICFGContext.cfgGraphs.put(body, new ExceptionalUnitGraph(body));
        ICFGContext.bodyLoops.put(body, new LoopFinder().getLoops(body));


//        new LoopFinder().getLoops(body).forEach(loop -> {
//            Stmt head = loop.getHead();
//            new LoopPathUtil(head).runPath().forEach(path -> {
//                logger.info(path.getCFGPath());
//            });
//        });
        new LoopFinder().getLoops(body).forEach(loop -> {

            new LoopPathUtil(loop.getHead(), loop).runPath().forEach(basePathUtil -> {
                logger.info(basePathUtil.getCFGPath());
            });
        });

//        body.getUnits().stream().filter(Analyzer::isRequest).forEach(unit -> {
//            if (new Analyzer(body).isLeakage((InvokeStmt) unit)) {
//                logger.info(body.getMethod());
//            }
//        });


//        List<Unit> units = new ArrayList<>(body.getUnits());
//        Set<CFGPath> paths = PathExtractor.extractPath(units.get(0));
//        paths.forEach(logger::info);
//        new B().funcA();
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


    abstract class E {

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

    class C {
        protected A a;

        C() {
            a = new A();
        }

        void run() {
            a.funcB();
        }
    }

    class D extends C {
        protected B a;

        D() {
            a = new B();
        }
    }
}
