package cn.edu.sustech.cse.sqlab.leakdroid.cmdparser;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.edu.sustech.cse.sqlab.leakdroid.exceptions.ParseOptionsException;
import com.sun.istack.NotNull;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.dongliu.apk.parser.exception.ParserException;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

public class OptionsArgs {
    private final static Logger logger = Logger.getLogger(OptionsArgs.class);
    private static File outputDir;
    private static File inputApkFile;
    private static File androidSdkFolder;
    private static File convertedJarFile;
    private static File temporaryWorkingDirectory;
    private static ApkFile inputApkFileInfo;
    private static HashMap<Integer, String> androidLibMap;

    private static boolean isVerboseMode = true;
    private static boolean outputAllDot = false;
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
            "com.bumptech.glide.*"
    );

    public static List<String> includedPackageNames = Arrays.asList(
            "java.*",
            "javax.*"
    );

    public static void initialOptions() {
        initialOutputDir();
        initialInputFile();
        initialInputApkFileInfo();
        initialAndroidSdkFolder();
        initialAndroidLibMap();
        initialTemporaryWorkingDirectory();
        initialOutputAllDot();
    }

    private static void initialOutputDir() {
        CommandLine cmdLine = OptionsParser.getCommandLine();
        String outputDirPath = cmdLine.hasOption(OptName.shortOutputDir) ?
                cmdLine.getOptionValue(OptName.shortOutputDir) : "./output/ssaOutput";
        outputDir = new File(outputDirPath);
        if (cmdLine.hasOption(OptName.shortOverrideOutputDir)) {
            logger.info("Start to clean up");
            try (Stream<Path> walk = Files.walk(outputDir.toPath())) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                logger.error(String.format("Fail to delete file: %s", path.toString()));
                            }
                        });
                logger.info("Clean up ends");
            } catch (IOException e) {
                logger.info("Fail to clean up");
            }
        }
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

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

    private static void initialTemporaryWorkingDirectory() {
        try {
            temporaryWorkingDirectory = Files.createTempDirectory("").toAbsolutePath().normalize().toFile();
        } catch (IOException e) {
            logger.error("Failed to create temporary working directory");
        }
    }

    private static void initialAndroidLibMap() {
        if (androidSdkFolder == null) {
            return;
        }
        androidLibMap = new HashMap<>();

        try (Stream<Path> walk = Files.walk(androidSdkFolder.toPath())) {
            List<String> files = walk.filter(Files::isRegularFile)
                    .map(Path::toString).collect(Collectors.toList());
            files.forEach(file -> {
                if (!file.endsWith(".jar")) {
                    return;
                }
                try (ApkFile apkFile = new ApkFile(file)) {
                    Integer targetSdkVersion = Integer.parseInt(apkFile.getApkMeta().getTargetSdkVersion());
                    androidLibMap.put(targetSdkVersion, file);
                } catch (ParserException pe) {
                    // ignore
                } catch (IOException e) {
                    logger.error(String.format("Error occurs: %s", e.toString()));
                }
            });
        } catch (IOException e) {
            logger.error(String.format("Error occurs: %s", e.toString()));
        }
    }

    private static void initialInputApkFileInfo() {
        if (inputApkFile == null) {
            return;
        }
        try {
            OptionsArgs.inputApkFileInfo = new ApkFile(inputApkFile);
        } catch (IOException e) {
            logger.info(String.format("Error occurs: %s", e.toString()));
        }
    }

    private static void initialOutputAllDot() {
        CommandLine cmdLine = OptionsParser.getCommandLine();
        outputAllDot = cmdLine.hasOption(OptName.shortOutputAllDot);
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

    public static File getConvertedJarFile() {
        return convertedJarFile;
    }

    public static void setConvertedJarFile(@NotNull File convertedJarFile) {
        OptionsArgs.convertedJarFile = convertedJarFile;
    }

    public static File getTemporaryWorkingDirectory() {
        return temporaryWorkingDirectory;
    }

    public static HashMap<Integer, String> getAndroidLibMap() {
        return androidLibMap;
    }

    public static ApkFile getInputApkFileInfo() {
        return inputApkFileInfo;
    }

    public static boolean isVerboseMode() {
        return isVerboseMode;
    }

    public static boolean outputAllDot() {
        return outputAllDot;
    }
}
