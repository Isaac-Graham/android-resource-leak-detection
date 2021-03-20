package cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages;

import java.io.IOException;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/3 22:17
 */
public abstract class BaseStage {

    public abstract void run() throws IOException;

    public abstract String getStageName();
}
