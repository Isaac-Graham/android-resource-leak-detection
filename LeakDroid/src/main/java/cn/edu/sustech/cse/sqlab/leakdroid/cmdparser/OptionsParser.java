package cn.edu.sustech.cse.sqlab.leakdroid.cmdparser;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static cn.edu.sustech.cse.sqlab.leakdroid.entities.Error.PARSE_ERROR;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2020/11/26 22:40
 */
public class OptionsParser {
    private static final Logger logger = Logger.getLogger(OptionsParser.class);
    private static final Options options = new Options();
    public static CommandLine commandLine;

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
        options.addOption(Option.builder(OptName.shortOverrideOutputDir)
                .longOpt(OptName.longOverrideOutputDir)
                .hasArg(false)
                .type(boolean.class)
                .desc("Whether override the output directory or not")
                .build());
        options.addOption(Option.builder(OptName.shortOutputAllDot)
                .longOpt(OptName.longOutputAllDot)
                .hasArg(false)
                .type(boolean.class)
                .desc("Whether output all dot file(containing those does not leak)")
                .build());
        options.addOption(Option.builder(OptName.shortPackageOnly)
                .longOpt(OptName.longPackageOnly)
                .hasArg(false)
                .type(boolean.class)
                .desc("Only the class in package will be analyzed if set")
                .build());
        options.addOption(Option.builder(OptName.shortAllLeakPaths)
                .longOpt(OptName.longAllLeakPaths)
                .hasArg(false)
                .type(boolean.class)
                .desc("Output all possible leak paths if plag set")
                .build());
        options.addOption(Option.builder(OptName.shortTimeLimit)
                .longOpt(OptName.longTimeLimit)
                .argName("time limit for analyzer (unit: s)")
                .hasArg(true)
                .type(int.class)
                .desc("Time limit of analyzer for a single method (unit: s)")
                .build());
        options.addOption(Option.builder(OptName.shortOnlyLeakPath)
                .longOpt(OptName.longOnlyLeakPath)
                .hasArg(false)
                .type(boolean.class)
                .desc("Only output leak path if flag is set")
                .build());
        options.addOption(Option.builder(OptName.shortOnlyResourceMethod)
                .longOpt(OptName.longOnlyResourceMethod)
                .hasArg(false)
                .type(boolean.class)
                .desc("Only method with resource requested will be output if flag is set")
                .build());
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Error occurs while parsing command line arguments.\n " + getHelpString());
            System.exit(PARSE_ERROR.errorCode);
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
        String help = byteArrayOutputStream.toString();
        printWriter.close();
        return help;
    }

}
