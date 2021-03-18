package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.UnitMethodNameTag;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import org.apache.log4j.Logger;
import soot.*;
import soot.util.Chain;

import java.util.Map;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/13 20:34
 */

@PhaseName(name = "wstp.icfg.drawer")
public class ICFGGenerator extends SceneTransformer {
    private final static Logger logger = Logger.getLogger(ICFGGenerator.class);

    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        UnitUtil.addMethodTag(Scene.v().getClasses());
    }
}
