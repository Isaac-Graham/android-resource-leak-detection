package cn.edu.sustech.cse.sqlab.leakdroid;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.dongliu.apk.parser.ApkFile;

public class CommandOptions {
    public static File outputDir = new File("./test_soot_output");
    public static boolean isVerboseMode = true;
    public static boolean generateSleepingApk = true;

    public static boolean processClassInPackageIdOnly = true;
    public static List<String> excludedPackageNames = Arrays.asList(
            "android.*",
            "androidx.*",
            "com.android.*",
            "com.google.android.*",
            "java.*",
            "javax.*",
            "kotlin.*",
            "kotlinx.*",
            "io.reactivex.*",
            "rx.*",
            "okhttp3.*",
            "okio.*",
            "com.squareup.okhttp.*",
            "com.alibaba.fastjson.*",
            "com.google.protobuf.*",
            "com.bumptech.glide.*"
    );

    public static ApkFile apkFileInfo;

}
