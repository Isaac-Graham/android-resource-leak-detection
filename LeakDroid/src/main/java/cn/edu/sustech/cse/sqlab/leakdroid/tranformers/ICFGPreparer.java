package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.UnitMethodNameTag;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import org.apache.log4j.Logger;
import soot.*;

import java.util.Map;
import java.util.Set;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/13 20:34
 */

@PhaseName(name = "wstp.icfg.drawer")
public class ICFGPreparer extends SceneTransformer {
    private final static Logger logger = Logger.getLogger(ICFGPreparer.class);

    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        ensureSoot();
        addMethodTag();
        addResourceRequestMethod();
        collectMethodsContainingResource();
    }

    private static void addMethodTag() {
        Scene.v().getClasses().forEach(sootClass -> {
            sootClass.getMethods().forEach(sootMethod -> {
                if (!sootMethod.hasActiveBody()) return;
                Body body = sootMethod.getActiveBody();
                body.getUnits().forEach(unit -> {
                    unit.addTag(new UnitMethodNameTag(sootMethod));
                });
            });
        });
    }

    private static void addResourceRequestMethod() {
        Scene.v().getClasses().forEach(sootClass -> {
            sootClass.getMethods().forEach(sootMethod -> {
                if (ResourceUtil.isResource(sootMethod.getReturnType())) {
                    ResourceUtil.addRequestMethod(sootMethod.getReturnType(), sootMethod);
                }
                if (sootMethod.isConstructor() && ResourceUtil.isResource(sootClass.getType())) {
                    ResourceUtil.addRequestMethod(sootMethod.getReturnType(), sootMethod);
                }
            });
        });
    }

    private static void ensureSoot() {
        Scene.v().getClasses().forEach(sootClass -> {
            sootClass.getMethods().forEach(sootMethod -> {
                if (!sootMethod.hasActiveBody()) return;
                SootMethodUtil.ensureSSA(sootMethod);
                SootMethodUtil.updateLocalName(sootMethod);
            });
        });
    }

    private static void collectMethodsContainingResource() {
        Set<SootMethod> requestedMethods = ResourceUtil.getAllRequestMethods();
        Scene.v().getCallGraph().forEach(edge -> {
            if (!edge.src().hasActiveBody()) return;
            if (requestedMethods.contains(edge.tgt())) {
                ResourceUtil.addMethodContainingResource(edge.src());
            }
        });
    }
}
