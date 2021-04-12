package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.newPkg.pathextractor;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import soot.Body;
import soot.Unit;
import soot.jimple.InvokeStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/4/10 4:36
 */
public class PathExtractor {
    private static final Logger logger = Logger.getLogger(PathExtractor.class);

    public static void extractPath(Unit startUnit, List<Unit> allUnits) {
        // 需要在每一个Unit都维护一个已经遍历了的successors节点
        // TODO: update callBackUnit to a list of units that been traversed.
        logger.error(String.format("Path extraction starts: %s", SootMethodUtil.getFullName(UnitUtil.getSootMethod(startUnit))));
        ExtractorUtil extractorUtil = new ExtractorUtil(startUnit, allUnits);
        while (!extractorUtil.dfsStack.empty()) {
            Unit topUnit = extractorUtil.dfsStack.peek();
            List<Unit> successors = getSuccessors(topUnit)
                    .stream()
                    .filter(successor -> extractorUtil.meetTimes.get(successor) != TraverseCount.TWICE
                            && extractorUtil.notMetSuccessors.get(topUnit).contains(successor))
                    .collect(Collectors.toList());
            if (successors.isEmpty()) {
                if (pathIsEnd(topUnit)) {
//                    reportPath(extractorUtil.dfsStack);
                }
                extractorUtil.callBack();
            } else {
                Unit successor = successors.get(0);
                extractorUtil.meetUnit(topUnit, successor);
            }
        }
        logger.error(String.format("Path extraction ends: %s", SootMethodUtil.getFullName(UnitUtil.getSootMethod(startUnit))));
    }


    private static void reportPath(Stack<Unit> dfsStack) {
        List<Unit> list = Lists.newArrayList(dfsStack);
        StringBuilder res = new StringBuilder();
        list.forEach(l -> {
            res.append(l).append(" -> ");
        });
        res.append("\n");
        logger.error(res);
    }


    private static boolean pathIsEnd(Unit topUnit) {
        ExceptionalUnitGraph unitGraph = ICFGContext.getCFGFromUnit(topUnit);
        if (unitGraph == null) {
            logger.warn("CFG is null");
            return true;
        }
        return unitGraph.getTails().contains(topUnit);
    }

    private static List<Unit> getSuccessors(Unit unit) {
        List<Unit> successors = new ArrayList<>();
        if (unit != null) {
            ExceptionalUnitGraph cfg = ICFGContext.getCFGFromUnit(unit);
            cfg.getSuccsOf(unit).forEach(successor -> {
                if (ResourceUtil.isRequest(unit) && UnitUtil.isCaughtExceptionRef(successor)) return;
                if (!(unit instanceof InvokeStmt) && UnitUtil.isCaughtExceptionRef(successor)) return;
//                if (successor == callBackUnit) return;
                successors.add(successor);
            });
        }
        return successors;
    }

    private enum TraverseCount {
        ZERO(0), ONCE(1), TWICE(2);
        int num;

        TraverseCount(int num) {
            this.num = num;
        }

        TraverseCount next() {
            if (this == ZERO) {
                return ONCE;
            } else if (this == ONCE) {
                return TWICE;
            } else {
                return null;
            }
        }

        TraverseCount last() {
            if (this == ZERO) {
                return null;
            } else if (this == ONCE) {
                return ZERO;
            } else {
                return ONCE;
            }
        }
    }

    private static class ExtractorUtil {
        HashMap<Unit, HashMap<Unit, Integer>> notMetSuccessors;
        HashMap<Unit, TraverseCount> meetTimes;
        Stack<Unit> dfsStack;
        final int defaultMaxMeetSuccessors = 2;

        ExtractorUtil(Unit startUnit, List<Unit> allUnits) {
            initialNotMetSuccessors(allUnits);
            initialMeetTime(startUnit, allUnits);
            initialDfsStack(startUnit);
        }


        private void initialNotMetSuccessors(List<Unit> allUnits) {
            notMetSuccessors = new HashMap<>();
            allUnits
            allUnits.forEach(this::addNotMetSuccessors);
        }

        private void initialMeetTime(Unit startUnit, List<Unit> allUnits) {
            meetTimes = new HashMap<>();
            allUnits.forEach(unit -> {
                meetTimes.put(unit, TraverseCount.ZERO);
            });
            meetTimes.put(startUnit, TraverseCount.ONCE);
        }

        private void initialDfsStack(Unit startUnit) {
            dfsStack = new Stack<>();
            dfsStack.add(startUnit);
        }

        private void addNotMetSuccessors(Unit unit) {
            ExceptionalUnitGraph cfg = ICFGContext.getCFGFromUnit(unit);
            if (cfg != null) {
                notMetSuccessors.put(unit, new HashSet<>(cfg.getSuccsOf(unit)));
            }
        }

        private void callBack() {
            Unit topUnit = dfsStack.pop();
            meetTimes.put(topUnit, meetTimes.get(topUnit).last());
            addNotMetSuccessors(topUnit);
        }

        private void meetUnit(Unit topUnit, Unit unit) {
            dfsStack.push(unit);
            meetTimes.put(unit, meetTimes.get(unit).next());
            notMetSuccessors.get(topUnit).remove(unit);
        }
    }
}
