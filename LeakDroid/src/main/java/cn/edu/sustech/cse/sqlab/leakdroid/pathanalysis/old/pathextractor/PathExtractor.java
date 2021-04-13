package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.pathextractor;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.utils.pathutils.BasePathUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.entities.cfgpath.BaseCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.utils.pathutils.PathUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import org.apache.log4j.Logger;
import soot.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 16:50
 */
@Deprecated
public class PathExtractor {
    private static final Logger logger = Logger.getLogger(PathExtractor.class);

    public static List<BaseCFGPath> extractPath(Unit unit) {
        logger.info(String.format("Method path extraction starts: %s", UnitUtil.getSootMethod(unit)));
        List<BasePathUtil> paths = new PathUtil(unit).runPath();
        List<BaseCFGPath> res = new ArrayList<>();
        paths.forEach(path -> {
            res.add(path.getCFGPath());
        });
        logger.info(String.format("Method path extraction ends: %s", UnitUtil.getSootMethod(unit)));
        return res;
    }
}
