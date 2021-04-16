package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootClassUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import org.apache.log4j.Logger;
import soot.*;
import soot.toolkits.graph.*;
import soot.util.cfgcmd.CFGToDotGraph;
import soot.util.dot.DotGraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;

import static cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier.*;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/3 22:53
 */

@PhaseName(name = "stp.drawcfg")
public class CFGDrawer extends BodyTransformer {
    private final static Logger logger = Logger.getLogger(CFGDrawer.class);
    private static final String leakFolder = "leak";
    private static final String notLeakFolder = "not_leak";
    private static final String unknownFolder = "unknown";
    private static final String noResourcesFolder = "no_resources";


    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        if (SootClassUtil.isExclude(body.getMethod().getDeclaringClass())) return;
        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;
//        if (true) {
//            return;
//        }

//        if (!SootMethodUtil.getFullName(body.getMethod()).contains("org.sufficientlysecure.keychain.keyimport.HkpKeyserver.add"))
//            return;
        DotGraph dotGraph = generateDotGraphPlanA(body);
        printDotGraph(body, dotGraph);
    }

    private static DotGraph generateDotGraphPlanA(Body body) {
        ExceptionalUnitGraph cfg = ICFGContext.getCFGFromMethod(body.getMethod());
        DotGraph dotGraph = new DotGraph(String.format("CFG of %s", SootMethodUtil.getFullName(body.getMethod())));
        if (cfg == null) return dotGraph;

        body.getUnits().forEach(unit -> {
            if (unit.hasTag(ResourceLeakTag.name)) {
                dotGraph.drawNode(getNodeName(unit)).setAttribute("color", "red");
            } else {
                dotGraph.drawNode(getNodeName(unit)).setAttribute("color", "black");
            }
            List<Unit> successors = cfg.getSuccsOf(unit);
            successors.forEach(successor -> {
                if (unit.hasTag(ResourceLeakTag.name)
                        && UnitUtil.getResourceLeakTag(unit).getSuccessors().contains(successor)) {
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

    private static String getNodeName(Unit unit) {
        return unit.toString();
    }

    private static void printDotGraph(Body body, DotGraph dotGraph) {
        LeakIdentifier leakIdentifier = ICFGContext.getMethodLeakIdentifier(body.getMethod());

        if (leakIdentifier != LEAK && !OptionsArgs.outputAllDot) {
            logger.info(String.format("Skip drawing %s", SootMethodUtil.getFullName(body.getMethod())));
            return;
        }
        logger.info(String.format("Drawing CFG of %s method", SootMethodUtil.getFullName(body.getMethod())));
        String baseFolder = getFolderFromLeakIdentifier(leakIdentifier);
        String packageName = SootMethodUtil.getFolderName(body.getMethod());
        Path path = Paths.get(OptionsArgs.outputDir.getAbsolutePath(),
                baseFolder,
                packageName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)));

        String predecessorPath = OptionsArgs.outputDir.getAbsolutePath();
        try {
            Path pathCreate = Files.createDirectories(path);
            if (pathCreate.toFile().exists()) {
                predecessorPath = pathCreate.toString();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            // ignore
        }
        dotGraph.plot(Paths.get(predecessorPath,
                SootMethodUtil.getFileNameString(body.getMethod())
        ).toString());

        logger.info(String.format("CFG of %s method drawn", SootMethodUtil.getFullName(body.getMethod())));
    }

    private static String getFolderFromLeakIdentifier(LeakIdentifier identifier) {
        if (identifier == LEAK) return leakFolder;
        else if (identifier == NOT_LEAK) return notLeakFolder;
        else if (identifier == NO_RESOURCES) return noResourcesFolder;
        else return unknownFolder;
    }
}
