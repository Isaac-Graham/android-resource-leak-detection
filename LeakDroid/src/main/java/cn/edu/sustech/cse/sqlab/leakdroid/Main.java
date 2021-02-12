package cn.edu.sustech.cse.sqlab.leakdroid;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsParser;
import cn.edu.sustech.cse.sqlab.leakdroid.stages.BaseStage;
import cn.edu.sustech.cse.sqlab.leakdroid.stages.MainStage;
import org.apache.log4j.Logger;

import java.io.IOException;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        OptionsParser.parse(args);
        OptionsArgs.initialOptions();

        BaseStage stagePipeLine = new MainStage();
        stagePipeLine.run();
//        RunSootStage analyzer = new RunSootStage();
//        analyzer.run();
    }
}

