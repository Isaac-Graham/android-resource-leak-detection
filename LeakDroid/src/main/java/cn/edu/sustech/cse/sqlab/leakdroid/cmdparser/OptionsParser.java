package cn.edu.sustech.cse.sqlab.leakdroid.cmdparser;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2020/11/26 22:40
 */
public class OptionsParser {
    private static final Logger logger = Logger.getLogger(OptionsParser.class);
    private static final Options options = new Options();
    private static CommandLine commandLine;

    public static void parse(String[] args) {
        initCliArgs(args);
    }

    private static void initCliArgs(String[] args) {
        CommandLineParser commandLineParser = new DefaultParser();
        options.addOption(Option.builder(OptName.shortAndroidSdkPath)
                .longOpt(OptName.longAndroidSdkPath)
                .required()
                .argName("android sdk path")
                .hasArg(true)
                .type(String.class)
                .desc("Path to a directory containing Android SDK jars")
                .build());
        options.addOption(Option.builder(OptName.shortInputApkFile)
                .longOpt(OptName.longInputApkFile)
                .required()
                .argName("input apk file")
                .hasArg(true)
                .type(String.class)
                .desc("Path to input APK file")
                .build());
        options.addOption(Option.builder(OptName.shortOutputDir)
                .longOpt(OptName.longOutputDir)
                .argName("output folder")
                .hasArg(true)
                .type(String.class)
                .desc("Path to output folder")
                .build());
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Error occurs while parsing command line arguments.\n " + getHelpString());
            System.exit(-1);
        }
    }

    public static CommandLine getCommandLine() {
        return commandLine;
    }

    private static String getHelpString() {
        HelpFormatter helpFormatter = new HelpFormatter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "scp -help", null,
                options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        printWriter.flush();
        String help = new String(byteArrayOutputStream.toByteArray());
        printWriter.close();
        return help;
    }

}
