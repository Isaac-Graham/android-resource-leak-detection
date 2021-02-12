package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import org.apache.log4j.Logger;
import soot.*;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.dot.DotGraph;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
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

        ExceptionalUnitGraph cfg = new ExceptionalUnitGraph(body);

        DotGraph dotGraph = new DotGraph(String.format("CFG of %s", body.getMethod()));
        body.getUnits().forEach(unit -> {
            dotGraph.drawNode(unit.toString()).setAttribute("color", "black");

            List<Unit> successors = cfg.getSuccsOf(unit);
            successors.forEach(successor -> {
                dotGraph.drawEdge(unit.toString(), successor.toString()).setAttribute("color", "black");
            });
        });


        logger.info(String.format("%s.dot", body.getMethod().getName()));
        dotGraph.plot(Paths.get(OptionsArgs.getOutputDir().getAbsolutePath(),
                String.format("%s.dot", body.getMethod().getName())).toString());

        logger.info(String.format("CFG of %s method drawn", body.getMethod().toString()));
    }

    private static String getFileName(SootMethod sootMethod) {
        String res = String.format("%s_%s_%s", sootMethod.getDeclaringClass().toString(),
                sootMethod.getReturnType().toString(),
                sootMethod.getName());
        return res;
//
//        String params = "";
//        for (int i = 0; i < sootMethod.getParameterCount(); i++) {
//
//        }
    }
}
