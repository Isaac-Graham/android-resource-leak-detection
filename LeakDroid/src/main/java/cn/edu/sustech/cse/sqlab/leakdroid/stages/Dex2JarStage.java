package cn.edu.sustech.cse.sqlab.leakdroid.stages;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.stages.dex2Jar.Dex2Jar;
import cn.edu.sustech.cse.sqlab.leakdroid.stages.dex2Jar.Dex2Jar2_0;

import java.io.IOException;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/3 22:18
 */
public class Dex2JarStage extends BaseStage {
    protected String stageName = "Dex2Jar";

    @Override
    public void run() throws IOException {
        Dex2Jar dex2Jar = new Dex2Jar2_0();
        dex2Jar.convert2Jar(OptionsArgs.getInputApkFile(), OptionsArgs.getTemporaryWorkingDirectory());
    }
}
