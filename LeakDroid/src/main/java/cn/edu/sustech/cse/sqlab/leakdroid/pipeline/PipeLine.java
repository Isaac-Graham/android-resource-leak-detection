package cn.edu.sustech.cse.sqlab.leakdroid.pipeline;

import cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages.BaseStage;
import cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages.Dex2JarStage;
import cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages.RunSootStage;
import cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages.SetupWorkingEnvironmentStage;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/3 22:27
 */
public class PipeLine{
    private final static Logger logger = Logger.getLogger(PipeLine.class);

    public void run() {
        List<BaseStage> stages = ImmutableList.of(
                new SetupWorkingEnvironmentStage(),
                new Dex2JarStage(),
                new RunSootStage()
        );
        stages.forEach(stage -> {
            logger.info(stage.getClass());
            try {
                logger.info(String.format("Start %s", stage.getStageName()));
                stage.run();
                logger.info(String.format("End %s", stage.getStageName()));
            } catch (IOException e) {
                logger.error(String.format("Error occurs in %s", stage.getStageName()));
            }
        });
    }
}
