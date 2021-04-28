package cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptName;
import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.exceptions.ParseOptionsException;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.exception.ParserException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs.androidLib;
import static cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs.cmdLine;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/20 16:57
 */
public class SetupWorkingEnvironmentStage extends BaseStage {
    private static final Logger logger = Logger.getLogger(SetupWorkingEnvironmentStage.class);
    public static final String stageName = "Setup Working Environment Stage";

    @Override
    public void run() {
        OptionsArgs.outputAllDot = cmdLine.hasOption(OptName.shortOutputAllDot);
        OptionsArgs.overrideOutputDir = cmdLine.hasOption(OptName.shortOverrideOutputDir) && !cmdLine.hasOption(OptName.shortDebugMode);
        OptionsArgs.onlyPackage = cmdLine.hasOption(OptName.shortPackageOnly);
        OptionsArgs.outputAllLeakPaths = cmdLine.hasOption(OptName.shortAllLeakPaths);
        OptionsArgs.onlyLeakPath = cmdLine.hasOption(OptName.shortOnlyLeakPath);
        OptionsArgs.onlyResourceMethod = cmdLine.hasOption(OptName.shortOnlyResourceMethod);
        OptionsArgs.debugMode = cmdLine.hasOption(OptName.shortDebugMode);
        if (cmdLine.hasOption(OptName.shortTimeLimit)) {
            OptionsArgs.timeLimit = Integer.parseInt(cmdLine.getOptionValue(OptName.shortTimeLimit));
        }
        initialInputFile();
        initialInputApkFileInfo();
        initialOutputDir();
        cleanUpOutputDir();
        initialAndroidSdkFolder();
        initialAndroidLibMap();
        initialTemporaryWorkingDirectory();
        initialIncludedPackageNames();
    }

    @Override
    public String getStageName() {
        return stageName;
    }

    private static void cleanUpOutputDir() {
        if (OptionsArgs.overrideOutputDir) {
            logger.info("Start to clean up");
            try (Stream<Path> walk = Files.walk(OptionsArgs.outputDir.toPath())) {
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
    }

    private static void initialOutputDir() {
        if (OptionsArgs.inputApkFileInfo == null) {
            throw new ParseOptionsException("Error occurs: Apk file info not found");
        }
        String fileName = OptionsArgs.inputApkFile.getName();
        String directoryName = fileName.substring(0, fileName.lastIndexOf("."));
        String outputDirPath = cmdLine.hasOption(OptName.shortOutputDir) ?
                String.format("%s/%s", cmdLine.getOptionValue(OptName.shortOutputDir), directoryName) : "./output/ssaOutput";
        OptionsArgs.outputDir = new File(outputDirPath);
        if (!OptionsArgs.outputDir.exists()) {
            OptionsArgs.outputDir.mkdirs();
        }
    }

    private static void initialInputFile() {
        String inputApkFilePath = cmdLine.getOptionValue(OptName.shortInputApkFile);
        OptionsArgs.inputApkFile = new File(inputApkFilePath);
        if (!OptionsArgs.inputApkFile.exists()) {
            throw new ParseOptionsException("Input Apk file does not exist");
        }
    }

    private static void initialAndroidSdkFolder() {
        String androidSdkFolderPath = cmdLine.getOptionValue(OptName.shortAndroidSdkPath);
        OptionsArgs.androidSdkFolder = new File(androidSdkFolderPath);
        if (!OptionsArgs.androidSdkFolder.exists()) {
            throw new ParseOptionsException("Android Sdk Folder does not exist");
        }
    }

    private static void initialTemporaryWorkingDirectory() {
        try {
            OptionsArgs.temporaryWorkingDirectory = Files.createTempDirectory("").toAbsolutePath().normalize().toFile();
        } catch (IOException e) {
            throw new ParseOptionsException("Failed to create temporary working directory");
        }
    }

    private static void initialAndroidLibMap() {
        if (OptionsArgs.androidSdkFolder == null) {
            return;
        }
        int apiLevel = 0;
        try {
            apiLevel = Integer.parseInt(OptionsArgs.inputApkFileInfo.getApkMeta().getTargetSdkVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (apiLevel == 0) {
            throw new ParseOptionsException("Failed to recognize api level");
        }


        try (Stream<Path> walk = Files.walk(OptionsArgs.androidSdkFolder.toPath())) {
            List<String> files = walk.filter(Files::isRegularFile)
                    .map(Path::toString).collect(Collectors.toList());
            int finalApiLevel = apiLevel;
            files.forEach(filePath -> {
                if (!filePath.endsWith(".jar")) {
                    return;
                }
                try (ApkFile apkFile = new ApkFile(filePath)) {
                    Integer targetSdkVersion = Integer.parseInt(apkFile.getApkMeta().getTargetSdkVersion());
                    if (targetSdkVersion == finalApiLevel) {
                        androidLib = filePath;
                    }
                } catch (ParserException pe) {
                    // ignore
                } catch (IOException e) {
                    logger.error(String.format("Error occurs: %s", e.toString()));
                }
            });
        } catch (IOException e) {
            logger.error(String.format("Error occurs: %s", e.toString()));
        }


        if (androidLib == null || androidLib.isEmpty()) {
            throw new ParseOptionsException(String.format("Failed to locate the corresponding android library: %d", apiLevel));
        }
    }

    private static void initialInputApkFileInfo() {
        if (OptionsArgs.inputApkFile == null) {
            throw new ParseOptionsException("Error occurs: Apk file not found.");
        }
        try {
            OptionsArgs.inputApkFileInfo = new ApkFile(OptionsArgs.inputApkFile);
        } catch (IOException e) {
            logger.warn(String.format("Error occurs: %s", e.toString()));
        }
    }

    private static void initialIncludedPackageNames() {
        if (cmdLine.hasOption(OptName.shortPackageOnly)) {
            if (OptionsArgs.inputApkFileInfo == null) {
                throw new ParseOptionsException("Apk info has not been parsed");
            }
            try {
                String packageName = OptionsArgs.inputApkFileInfo.getApkMeta().getPackageName();
                String[] packages = packageName.split("\\.");
                if (packages.length > 2) {
                    packageName = String.format("%s.%s", packages[0], packages[1]);
                }
                OptionsArgs.includedPackageNames.add(String.format("%s.*", packageName));
            } catch (IOException e) {
                throw new ParseOptionsException("Fail to parse apk meta data");
            }
        }
    }

}
