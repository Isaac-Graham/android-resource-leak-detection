package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ResourceLeakDetector;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.pathextractor.CFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.pathextractor.PathExtractor;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootClassUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import soot.*;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.*;
import java.util.stream.Collectors;

import static cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier.NOT_LEAK;
import static cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier.NO_RESOURCES;

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
        ICFGContext.SetMethodLeakIdentifier(body.getMethod(), NO_RESOURCES);
        if (SootClassUtil.isExclude(body.getMethod().getDeclaringClass())) return;
        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;

//        if (!SootMethodUtil.getFullName(body.getMethod()).contains("com.irccloud.android.GingerbreadImageProxy.run"))
//            return;
        if (!ResourceUtil.getMethodContainingResource().contains(body.getMethod())) return;
        if (ResourceUtil.getAllRequestMethods().contains(body.getMethod())) return;
//        ResourceUtil.getAllRequestMethods().forEach(logger::info);
//        if (list.isEmpty()) {
//            logger.info(SootMethodUtil.getFullName(body.getMethod()));
//            body.getUnits().forEach(ResourceUtil::isRequest);
//        }
//        body.getUnits().forEach(unit -> {
//            if (unit.toString().contains("createSocket")) {
//                logger.info(unit);
//            }
//        });
//        logger.info(SootMethodUtil.getFullName(body.getMethod()));

//        if (body.getUnits().stream().noneMatch(ResourceUtil::isRequest)) {
//            logger.info(String.format("No resource requested in method: %s. Break", SootMethodUtil.getFullName(body.getMethod())));
//            res = NO_RESOURCES;
//        } else {
        logger.info(String.format("Start to analyze method: %s", SootMethodUtil.getFullName(body.getMethod())));

        LeakIdentifier res = ResourceLeakDetector.detect(body, new HashSet<>());

        logger.info(String.format("End analyze method: %s", SootMethodUtil.getFullName(body.getMethod())));
//
//        }
        ICFGContext.SetMethodLeakIdentifier(body.getMethod(), res);
    }


}
