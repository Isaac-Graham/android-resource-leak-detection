package cn.edu.sustech.cse.sqlab.leakdroid.cmdparser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dongliu.apk.parser.ApkFile;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

public class OptionsArgs {
    private final static Logger logger = Logger.getLogger(OptionsArgs.class);
    public static File outputDir;
    public static File inputApkFile;
    public static File androidSdkFolder;
    public static File convertedJarFile;
    public static File temporaryWorkingDirectory;
    public static ApkFile inputApkFileInfo;
    public static String androidLib;
    public static CommandLine cmdLine = OptionsParser.getCommandLine();

    public static int timeLimit = 10;
    public static boolean debugMode = false;
    public static boolean isVerboseMode = true;
    public static boolean outputAllDot = false;
    public static boolean overrideOutputDir = false;
    public static boolean onlyPackage = false;
    public static boolean outputAllLeakPaths = false;
    public static boolean onlyLeakPath = false;
    public static boolean onlyResourceMethod = false;
    public static List<String> excludedPackageNames = Arrays.asList(
            "android.*",
            "androidx.*",
            "com.android.*",
            "com.google.android.*",
            "kotlin.*",
            "kotlinx.*",
            "io.reactivex.*",
            "rx.*",
            "okhttp3.*",
            "okio.*",
            "com.squareup.okhttp.*",
            "com.alibaba.fastjson.*",
            "com.google.protobuf.*",
            "com.bumptech.glide.*",
            "java.*",
            "javax.*"
    );

    public static List<String> includedPackageNames = new ArrayList<>();
}
