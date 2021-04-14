package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.pathextractor;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.*;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/4/10 4:36
 */
public class PathExtractor {
    private static final Logger logger = Logger.getLogger(PathExtractor.class);
    public boolean isEnd = false;
    public List<CFGPath> extractPath(Unit startUnit) {
        List<CFGPath> paths = new ArrayList<>();
        SootMethod curMethod = UnitUtil.getSootMethod(startUnit);
        logger.info(String.format("Path extraction starts: %s", SootMethodUtil.getFullName(curMethod)));
        ExtractorUtil extractorUtil = new ExtractorUtil(startUnit);

        while (!extractorUtil.dfsStack.empty() && !isEnd) {
            Unit topUnit = extractorUtil.dfsStack.peek();
            List<Unit> successors = extractorUtil.getSuccessors();
            if (successors.isEmpty()) {
                if (pathIsEnd(topUnit)) {
                    paths.add(new CFGPath(Lists.newArrayList(extractorUtil.dfsStack)));
                }
                extractorUtil.callBack();
            } else {
                Unit successor = successors.get(0);
                extractorUtil.meetUnit(successor);
            }
        }

        logger.info(String.format("Path extraction ends: %s", SootMethodUtil.getFullName(curMethod)));
        return paths;
    }

    private static boolean pathIsEnd(Unit topUnit) {
        ExceptionalUnitGraph unitGraph = ICFGContext.getCFGFromUnit(topUnit);
        if (unitGraph == null) {
            logger.warn("CFG is null");
            return true;
        }
        return unitGraph.getTails().contains(topUnit);
    }

}
