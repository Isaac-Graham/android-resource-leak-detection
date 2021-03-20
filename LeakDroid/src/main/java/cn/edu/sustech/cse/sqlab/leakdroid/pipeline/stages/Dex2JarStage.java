package cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages.dex2Jar.Dex2Jar;
import cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages.dex2Jar.Dex2Jar2_0;

import java.io.IOException;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/3 22:18
 */
public class Dex2JarStage extends BaseStage {
    private static final String stageName = "Dex2Jar Stage";

    @Override
    public void run() throws IOException {
        Dex2Jar dex2Jar = new Dex2Jar2_0();
        dex2Jar.convert2Jar(OptionsArgs.inputApkFile, OptionsArgs.temporaryWorkingDirectory);
    }

    @Override
    public String getStageName() {
        return stageName;
    }
}
