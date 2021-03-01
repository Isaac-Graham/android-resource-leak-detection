package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import org.apache.log4j.Logger;
import soot.*;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.dot.DotGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/3 22:53
 */

@PhaseName(name = "stp.drawcfg")
public class CFGDrawer extends BodyTransformer {
    private final static Logger logger = Logger.getLogger(CFGDrawer.class);


    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        logger.info(String.format("Drawing CFG of %s method", body.getMethod()));

        ExceptionalUnitGraph cfg = new ExceptionalUnitGraph(body);

        DotGraph dotGraph = new DotGraph(String.format("CFG of %s", body.getMethod()));
        body.getUnits().forEach(unit -> {
            if (unit.hasTag(ResourceLeakTag.name)) {
                dotGraph.drawNode(unit.toString()).setAttribute("color", "red");
            } else {
                dotGraph.drawNode(unit.toString()).setAttribute("color", "black");
            }
            List<Unit> successors = cfg.getSuccsOf(unit);
            successors.forEach(successor -> {
                if (unit.hasTag(ResourceLeakTag.name) && successor.hasTag(ResourceLeakTag.name)) {
                    dotGraph.drawEdge(unit.toString(), successor.toString()).setAttribute("color", "red");
                } else {
                    dotGraph.drawEdge(unit.toString(), successor.toString()).setAttribute("color", "black");
                }
            });
        });

        String packageName = body.getMethod().getDeclaringClass().getPackageName();
        Path path = Paths.get(OptionsArgs.getOutputDir().getAbsolutePath(), packageName.replaceAll("\\.", "/"));

        String predecessorPath = OptionsArgs.getOutputDir().getAbsolutePath();
        try {
            Path pathCreate = Files.createDirectories(path);
            if (pathCreate.toFile().exists()) {
                predecessorPath = pathCreate.toString();
            }
        } catch (IOException e) {
            // ignore
        }
        dotGraph.plot(
                Paths.get(predecessorPath,
                        getFileName(body.getMethod())
                ).toString());

        logger.info(String.format("CFG of %s method drawn", body.getMethod().toString()));
    }

    private static String getFileName(SootMethod sootMethod) {
        return String.format("%s_%s.dot",
                sootMethod.getDeclaringClass().toString(),
                sootMethod.getName())
                .replace('<', 'l')
                .replace('>', 'r');
    }
}
