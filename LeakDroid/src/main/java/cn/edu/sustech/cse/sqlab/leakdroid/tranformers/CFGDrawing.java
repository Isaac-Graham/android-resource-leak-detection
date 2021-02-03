package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import org.apache.log4j.Logger;
import soot.Body;
import soot.BodyTransformer;
import soot.util.dot.DotGraph;

import java.util.Map;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/3 22:53
 */

@PhaseName(name = "stp.drawcfg")
public class CFGDrawing extends BodyTransformer {
    private final static Logger logger = Logger.getLogger(CFGDrawing.class);

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        logger.info(String.format("Drawing CFG of %s method", body.getMethod()));

        DotGraph dotGraph = new DotGraph(String.format("CFG of %s", body.getMethod()));
        body.getUnits().forEach(unit -> {
            logger.info(unit.toString());
        });
    }
}
