package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.pathutils.BasePathUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.pathutils.LoopPathUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.annotation.logic.Loop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 20:16
 */
public class LoopUtil {
    private static final HashMap<Unit, List<BasePathUtil>> loopPathsMap = new HashMap<>();

    public static boolean isLoopHead(Unit unit, SootMethod sootMethod) {
        Set<Loop> loops = ICFGContext.getLoopsFromMethod(sootMethod);
        if (loops == null || loops.isEmpty()) {
            return false;
        }
        return loops.stream().anyMatch(loop -> loop.getHead() == unit);
    }

    public static Loop getLoopFromHead(Unit unit, SootMethod sootMethod) {
        if (!isLoopHead(unit, sootMethod)) {
            return null;
        }
        Set<Loop> loops = ICFGContext.getLoopsFromMethod(sootMethod);
        for (Loop loop : loops) {
            if (loop.getHead() == unit) {
                return loop;
            }
        }
        return null;
    }

    public static List<BasePathUtil> getLoopPaths(Unit headUnit, SootMethod sootMethod) {
        if (!isLoopHead(headUnit, sootMethod)) {
            return new ArrayList<>();
        }
        if (loopPathsMap.containsKey(headUnit)) {
            return loopPathsMap.get(headUnit);
        }
        Loop loop = getLoopFromHead(headUnit, sootMethod);
        List<BasePathUtil> res = new LoopPathUtil(headUnit, loop, sootMethod).runPath();
        res.forEach(BasePathUtil::clearPathStatus);
        loopPathsMap.put(headUnit, res);
        return res;
    }
}
