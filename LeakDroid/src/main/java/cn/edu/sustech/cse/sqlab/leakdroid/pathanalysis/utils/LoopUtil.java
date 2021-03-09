package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils;

import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import soot.Body;
import soot.Unit;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;

import java.util.Set;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/9 15:08
 */
public class LoopUtil implements Cloneable {

    public static boolean isLoopHead(Unit unit) {
        Body body = ICFGContext.icfg.getBodyOf(unit);
        Set<Loop> loops = new LoopFinder().getLoops(body);
        return loops.stream().anyMatch(loop -> loop.getHead() == unit);
    }

    @Override
    public Object clone() {
        LoopUtil loopUtil = null;
        try {
            loopUtil = (LoopUtil) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return loopUtil;
    }
}
