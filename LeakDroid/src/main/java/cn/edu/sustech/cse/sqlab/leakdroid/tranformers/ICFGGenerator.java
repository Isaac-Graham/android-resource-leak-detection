package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import org.apache.log4j.Logger;
import soot.*;
import soot.util.queue.QueueReader;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/13 20:34
 */

@PhaseName(name = "wstp.icfg.drawer")
public class ICFGGenerator extends SceneTransformer {
    private final static Logger logger = Logger.getLogger(ICFGGenerator.class);

    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        ICFGContext.initializeUnitBodyMap(Scene.v().getApplicationClasses());
//        ICFGContext.initializeUnitToOwner();
//        Scene.v().
//
//        logger.info(ICFGContext.unitToOwner.size());
    }




}
