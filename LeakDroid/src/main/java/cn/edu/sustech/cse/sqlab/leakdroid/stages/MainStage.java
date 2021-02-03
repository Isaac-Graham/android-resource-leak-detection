package cn.edu.sustech.cse.sqlab.leakdroid.stages;

import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/3 22:27
 */
public class MainStage extends BaseStage {
    private final static Logger logger = Logger.getLogger(MainStage.class);

    @Override
    public void run() {
        List<BaseStage> stages = ImmutableList.of(new Dex2JarStage(), new RunSootStage());
        stages.forEach(stage -> {
            try {
                stage.run();
            } catch (IOException e) {
                logger.error(String.format("Error occurs in %s stage", stage.stageName));
            }
        });
    }
}
