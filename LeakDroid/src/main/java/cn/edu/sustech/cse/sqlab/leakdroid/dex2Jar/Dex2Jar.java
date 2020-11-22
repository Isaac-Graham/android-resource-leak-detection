package cn.edu.sustech.cse.sqlab.leakdroid.dex2Jar;

import java.io.File;

public abstract class Dex2Jar {
    public abstract File dex2Jar(File apkFile, File outputFolder);
}
