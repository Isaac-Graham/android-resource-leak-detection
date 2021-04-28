package cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import com.googlecode.d2j.reader.Op;
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
        removeJarFile();
    }

    private static void removeJarFile() {
        if (OptionsArgs.convertedJarFile != null
                && OptionsArgs.convertedJarFile.exists()
                && !OptionsArgs.debugMode) {
            if (OptionsArgs.convertedJarFile.delete()) {
                logger.info("Successfully delete converted jar file");
            } else {
                logger.error(String.format("Fail to delete converted jar file. Please remove it manually: %s", OptionsArgs.convertedJarFile.getAbsolutePath()));
            }
        }
    }

    @Override
    public String getStageName() {
        return stageName;
    }

}
