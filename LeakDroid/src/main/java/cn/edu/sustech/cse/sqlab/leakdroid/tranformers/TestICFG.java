package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ResourceLeakDetector;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.BaseCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.newPkg.pathextractor.PathExtractor;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootClassUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.apache.lucene.util.RamUsageEstimator;
import soot.*;

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
        if (SootClassUtil.isExclude(body.getMethod().getDeclaringClass())) return;
        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;

        logger.info(String.format("Start to analyze method: %s", SootMethodUtil.getFullName(body.getMethod())));
        if (body.getUnits().stream().noneMatch(ResourceUtil::isRequest)) {
            logger.info(String.format("No resource requested in method: %s. Break", SootMethodUtil.getFullName(body.getMethod())));
            return;
        }

        if (!SootMethodUtil.getFullName(body.getMethod()).contains("cn.edu.sustech.cse.sqlab.testSoot.MainActivity.loopTest"))
            return;
        body.getUnits().stream().filter(ResourceUtil::isRequest).forEach(unit -> {
            PathExtractor.extractPath(unit, Lists.newArrayList(body.getUnits()));
        });


//        body.getUnits().stream().filter(ResourceUtil::isRequest).forEach(unit -> {
//            try {
//                new ResourceLeakDetector(unit).detect();
//            } catch (StackOverflowError error) {
//                logger.error("Stack overflow occurs on: " + SootMethodUtil.getFullName(body.getMethod()));
//            }
//        });
//
//
//        logger.info(String.format("End analyze method: %s", SootMethodUtil.getFullName(body.getMethod())));
    }
}
