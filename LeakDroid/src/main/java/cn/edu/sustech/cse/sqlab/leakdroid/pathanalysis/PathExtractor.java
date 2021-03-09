package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.CFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.PathUtil;
import soot.Unit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/9 14:50
 */
public class PathExtractor {

    public static Set<CFGPath> extractPath(Unit startUnit) {
        Set<CFGPath> allPaths = new HashSet<>();
        Stack<PathUtil> unfinishedPaths = new Stack<>();
        unfinishedPaths.push(new PathUtil(startUnit));

        while (!unfinishedPaths.empty()) {
            PathUtil topUnfinishedPath = unfinishedPaths.pop();
            if (topUnfinishedPath.isEmpty()) {
                continue;
            } else if (topUnfinishedPath.isFinished()) {
                allPaths.add(topUnfinishedPath.getCFGPath());
                continue;
//                topUnfinishedPath.
            }
            List<PathUtil> subPaths = topUnfinishedPath.runPath();
            unfinishedPaths.addAll(subPaths);
        }
        return allPaths;
    }
}
