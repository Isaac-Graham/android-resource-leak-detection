package cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/4/13 20:30
 */
public class CleanUpStage extends BaseStage {
    private final static Logger logger = Logger.getLogger(CleanUpStage.class);
    protected static final String stageName = "CleanUp Stage";

    @Override
    public void run() throws IOException {
        StringBuilder sb = new StringBuilder();
        ICFGContext.processingMethods.forEach(method -> {
            sb.append(SootMethodUtil.getFullName(method)).append("\n");
        });
        logger.info(sb.toString());
    }

    @Override
    public String getStageName() {
        return stageName;
    }

}
