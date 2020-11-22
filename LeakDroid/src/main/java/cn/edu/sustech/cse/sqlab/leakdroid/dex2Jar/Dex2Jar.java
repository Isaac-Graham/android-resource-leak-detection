package cn.edu.sustech.cse.sqlab.leakdroid.dex2Jar;
/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2020/11/22 21:59
 */

import java.io.File;

public abstract class Dex2Jar {
    public abstract File dex2Jar(File apkFile, File outputFolder);
}
