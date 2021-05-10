package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ResourceLeakDetector;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootClassUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import org.apache.log4j.Logger;
import soot.*;

import java.util.*;

import static cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier.NO_RESOURCES;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/18 22:19
 */
@PhaseName(name = "stp.testicfg")
public class CFGRunner extends BodyTransformer {
    private final static Logger logger = Logger.getLogger(CFGRunner.class);


    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        ICFGContext.SetMethodLeakIdentifier(body.getMethod(), NO_RESOURCES);
        if (SootClassUtil.isExclude(body.getMethod().getDeclaringClass())) return;
        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;
        if (!ResourceUtil.getMethodContainingResource().contains(body.getMethod())) return;
        if (ResourceUtil.getAllRequestMethods().contains(body.getMethod())) return;
//        if (true)return;
//        if (!SootMethodUtil.getFullName(body.getMethod()).contains("org.sufficientlysecure.keychain.util.FileHelper.isEncryptedFile")) return;
//        ICFGContext.getCFGFromMethod(body.getMethod()).getTails().forEach(
//                unit -> logger.info(unit.getClass())
//        );
//        if (!ResourceUtil.getMethodContainingResource().contains(body.getMethod())) return;
//        if (ResourceUtil.getAllRequestMethods().contains(body.getMethod())) return;

        logger.info(String.format("Start to analyze method: %s", SootMethodUtil.getFullName(body.getMethod())));

        LeakIdentifier res = ResourceLeakDetector.detect(body, new HashSet<>());

        logger.info(String.format("End analyze method: %s", SootMethodUtil.getFullName(body.getMethod())));
//
//        }
        ICFGContext.SetMethodLeakIdentifier(body.getMethod(), res);
    }


}
