package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils;

import soot.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/9 14:51
 */
public class LoopPathUtil extends PathUtil implements Cloneable {
    public LoopPathUtil() {
    }

    public LoopPathUtil(Unit unit) {

    }

    public List<PathUtil> runPath() {
        this.removeCallBackEdges();
        List<PathUtil> loopPaths = super.runPath();

        List<PathUtil> pathUtils = new ArrayList<>();
        while (!pathStatus.isEnd()) {
            Stack<Unit> neighborStackTop = pathStatus.getNeighborTop();
            if (neighborStackTop.empty()) {
                if (cfgPath.isEnd()) {
                    pathUtils.add((PathUtil) this.clone());
                }
                callBack();
            } else {
                Unit nextUnit = neighborStackTop.pop();
                if (LoopUtil.isLoopHead(nextUnit)) {
//                    List<PathUtil> loopPaths = new LoopPathUtil(nextUnit).runPath();
//                    pathUtils.addAll(this.mergePath(loopPaths));
                    break;
                } else {
                    this.updatePath(nextUnit);
                }
            }
        }
        return mergedLoopPaths(loopPaths);
    }

    private void removeCallBackEdges() {

    }


    private List<PathUtil> mergedLoopPaths(List<PathUtil> loopPaths) {
        return loopPaths;
    }

    @Override
    public Object clone() {
        LoopPathUtil loopPathUtil = null;
        loopPathUtil = (LoopPathUtil) super.clone();
        return loopPathUtil;
    }
}
