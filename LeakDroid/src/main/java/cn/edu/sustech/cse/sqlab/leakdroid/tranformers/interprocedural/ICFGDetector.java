package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.*;
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
        if (!body.getMethod().getName().contains("foo")) return;
        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;
        SootMethodUtil.ensureSSA(body.getMethod());
        SootMethodUtil.updateLocalName(body.getMethod());

        body.getUnits().stream().filter(Analyzer::isRequest).forEach(unit -> {
            if (new Analyzer(body).isLeakage((InvokeStmt) unit)) {
                logger.info(body.getMethod());
            }
        });
    }
}
