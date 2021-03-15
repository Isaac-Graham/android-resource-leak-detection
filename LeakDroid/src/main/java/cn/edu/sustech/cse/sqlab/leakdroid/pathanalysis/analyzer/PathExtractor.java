package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.BaseCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.pathutils.BasePathUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.pathutils.PathUtil;
import soot.Body;
import soot.SootMethod;
import soot.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 16:50
 */
public class PathExtractor {

    public static List<BaseCFGPath> extractPath(Unit unit) {
        List<BasePathUtil> paths = new PathUtil(unit).runPath();
        List<BaseCFGPath> res = new ArrayList<>();
        paths.forEach(path -> {
            res.add(path.getCFGPath());
        });
        return res;
    }
}
