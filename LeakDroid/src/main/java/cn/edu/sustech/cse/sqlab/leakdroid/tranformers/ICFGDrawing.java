package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import org.apache.log4j.Logger;
import soot.SceneTransformer;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

import java.util.Map;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/13 20:34
 */

@PhaseName(name = "wstp.icfg")
public class ICFGDrawing extends SceneTransformer {
    public static JimpleBasedInterproceduralCFG icfg;
    private final static Logger logger = Logger.getLogger(ICFGDrawing.class);

    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        icfg = new JimpleBasedInterproceduralCFG();
    }
}
