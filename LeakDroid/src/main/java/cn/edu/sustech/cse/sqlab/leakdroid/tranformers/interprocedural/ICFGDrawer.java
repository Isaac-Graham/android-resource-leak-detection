package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import org.apache.log4j.Logger;
import soot.Body;
import soot.BodyTransformer;
import soot.SceneTransformer;
import soot.Unit;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/13 20:34
 */

@PhaseName(name = "wstp.icfg.drawer")
public class ICFGDrawer extends SceneTransformer {
    private final static Logger logger = Logger.getLogger(ICFGDrawer.class);

    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG();

        Set<Unit> startNodes = icfg.allNonCallStartNodes();
        List<Unit> nodes = new ArrayList<>(startNodes);
        nodes.forEach(node -> {
            logger.info(icfg.getMethodOf(node));
        });
//        logger.info(icfg.getOrCreateUnitGraph(icfg.getBodyOf(nodes.get(0))));
    }
}
