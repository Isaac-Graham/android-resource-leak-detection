package cn.edu.sustech.cse.sqlab.leakdroid.util;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.runSoot.SootAnalyzer;
import org.apache.log4j.Logger;
import soot.PackManager;
import soot.Transform;
import soot.Transformer;

import java.lang.annotation.Annotation;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2020/11/22 22:20
 */
public class PackManagerUtil {
    private static final Logger logger = Logger.getLogger(PackManagerUtil.class);

    public static void addTransformation(PackManager pm, Transformer t) {
        // TODO: require(T::class.annotations.any { it.annotationClass == PhaseName::class })
        Annotation[] annotations = t.getClass().getAnnotations();
        String phaseName = null;
        for (int i = 0; i < annotations.length; i++) {
            logger.info(annotations[i].annotationType());
            if (annotations[i].annotationType() == PhaseName.class) {
                phaseName = ((PhaseName) annotations[i]).name();
                break;
            }
        }
        // TODO: require(phaseName.matches("\\w+\\.\\w+".toRegex()))
        String packName = phaseName.split("\\.")[0];
        pm.getPack(packName).add(new Transform(phaseName, t));
    }
}
