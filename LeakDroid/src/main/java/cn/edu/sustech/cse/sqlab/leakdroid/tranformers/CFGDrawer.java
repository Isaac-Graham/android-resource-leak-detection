package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import org.apache.log4j.Logger;
import soot.*;
import soot.jbco.util.SimpleExceptionalGraph;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;
import soot.util.cfgcmd.CFGToDotGraph;
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
//        if (true) {
//            return;
//        }
        DotGraph dotGraph = generateDotGraphPlanA(body);
        printDotGraph(body, dotGraph);
    }

    private static DotGraph generateDotGraphPlanA(Body body) {
        ExceptionalUnitGraph cfg = ICFGContext.getCFGFromMethod(body.getMethod());
        DotGraph dotGraph = new DotGraph(String.format("CFG of %s", body.getMethod()));

        body.getUnits().forEach(unit -> {
            if (unit.hasTag(ResourceLeakTag.name)) {
                dotGraph.drawNode(getNodeName(unit)).setAttribute("color", "red");
            } else {
                dotGraph.drawNode(getNodeName(unit)).setAttribute("color", "black");
            }
            List<Unit> successors = cfg.getSuccsOf(unit);
            successors.forEach(successor -> {
                if (unit.hasTag(ResourceLeakTag.name) && UnitUtil.getResourceLeakTag(unit).getSuccessors().contains(successor)) {
                    dotGraph.drawEdge(getNodeName(unit), getNodeName(successor)).setAttribute("color", "red");
                } else {
                    dotGraph.drawEdge(getNodeName(unit), getNodeName(successor)).setAttribute("color", "black");
                }
            });
        });
        return dotGraph;
    }

    private static DotGraph generateDotGraphPlanB(Body body) {
        ExceptionalUnitGraph cfg = ICFGContext.getCFGFromMethod(body.getMethod());
        return new CFGToDotGraph().drawCFG(cfg);
    }

    private static String getFileName(SootMethod sootMethod) {
        return String.format("%s_%s.dot",
                sootMethod.getDeclaringClass().toString(),
                sootMethod.getName())
                .replace('<', 'l')
                .replace('>', 'r');
    }

    private static String getNodeName(Unit unit) {
        return String.format("%s\n%s", unit.toString(), unit.getClass().toString());
    }

    private static void printDotGraph(Body body, DotGraph dotGraph) {
        boolean leak = body.getUnits().stream().anyMatch(unit -> unit.hasTag(ResourceLeakTag.name));
        if (!leak && !OptionsArgs.outputAllDot) {
            logger.info(String.format("Skip drawing %s", body.getMethod()));
            return;
        }
        logger.info(String.format("Drawing CFG of %s method", body.getMethod()));
        String baseFolder = leak ? "leak" : "not_leak";
        String packageName = body.getMethod().getDeclaringClass().getPackageName();
        Path path = Paths.get(OptionsArgs.outputDir.getAbsolutePath(), baseFolder, packageName.replaceAll("\\.", "/"));

        String predecessorPath = OptionsArgs.outputDir.getAbsolutePath();
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

}
