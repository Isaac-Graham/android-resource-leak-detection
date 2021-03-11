package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import org.apache.log4j.Logger;
import soot.SceneTransformer;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

import java.util.HashMap;
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
        ICFGContext.icfg = new JimpleBasedInterproceduralCFG();
        ICFGContext.cfgGraphs = new HashMap<>();
        ICFGContext.bodyLoops = new HashMap<>();
    }
}
