package cn.edu.sustech.cse.sqlab.leakdroid.cmdparser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import cn.edu.sustech.cse.sqlab.leakdroid.exceptions.ParseOptionsException;
import org.apache.commons.cli.CommandLine;

public class CommandOptions {
    private static File outputDir;
    private static File inputApkFile;
    private static File androidSdkFolder;
    public static boolean isVerboseMode = true;
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

    public static void initialOptions() {
        initialOutputDir();
        initialInputFile();
        initialAndroidSdkFolder();
    }

    private static void initialOutputDir() {
        CommandLine cmdLine = OptionsParser.getCommandLine();
        String outputDirPath = cmdLine.hasOption(OptName.shortOutputDir) ?
                cmdLine.getOptionValue(OptName.shortOutputDir) : "./output/ssaOutput";
        outputDir = new File(outputDirPath);
    }

    private static void initialInputFile() {
        CommandLine cmdLine = OptionsParser.getCommandLine();
        String inputApkFilePath = cmdLine.getOptionValue(OptName.shortInputApkFile);
        inputApkFile = new File(inputApkFilePath);
        if (!inputApkFile.exists()) {
            throw new ParseOptionsException("Input Apk file does not exist");
        }
    }

    private static void initialAndroidSdkFolder() {
        CommandLine cmdLine = OptionsParser.getCommandLine();
        String androidSdkFolderPath = cmdLine.getOptionValue(OptName.shortAndroidSdkPath);
        androidSdkFolder = new File(androidSdkFolderPath);
        if (!androidSdkFolder.exists()) {
            throw new ParseOptionsException("Android Sdk Folder does not exist");
        }
    }

    public static File getOutputDir() {
        return outputDir;
    }

    public static File getInputApkFile() {
        return inputApkFile;
    }

    public static File getAndroidSdkFolder() {
        return androidSdkFolder;
    }

}
