package cn.edu.sustech.cse.sqlab.leakdroid.stages.dex2Jar;
/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2020/11/22 21:59
 */

import java.io.File;
import java.io.IOException;

public abstract class Dex2Jar {
    public abstract File convert2Jar(File apkFile, File outputFolder) throws IOException;
}
