package cn.edu.sustech.cse.sqlab.leakdroid.util;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import org.apache.log4j.Logger;
import soot.SootClass;

import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/23 20:27
 */
public class SootClassUtil {
    private static final Logger logger = Logger.getLogger(SootClassUtil.class);

    public static boolean isExclude(SootClass sootClass) {
        String className = sootClass.getName();
        if (OptionsArgs.onlyPackage) {
            return OptionsArgs.includedPackageNames
                    .stream()
                    .noneMatch(pkgName
                            -> className.contains(pkgName.substring(0, pkgName.length() - 1)));
        } else {
            return OptionsArgs.excludedPackageNames
                    .stream()
                    .anyMatch(pkgName
                            -> className.contains(pkgName.substring(0, pkgName.length() - 1)));
        }
    }

    public static String getFullName(SootClass sootClass) {
        return sootClass.getName();
    }

    public static String getShortName(SootClass sootClass) {
        return sootClass.getShortName();
    }
}
