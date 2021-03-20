package cn.edu.sustech.cse.sqlab.leakdroid;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsParser;
import cn.edu.sustech.cse.sqlab.leakdroid.pipeline.PipeLine;
import org.apache.log4j.Logger;

import java.io.IOException;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        OptionsParser.parse(args);

        PipeLine stagePipeLine = new PipeLine();
        stagePipeLine.run();
//        RunSootStage analyzer = new RunSootStage();
//        analyzer.run();
    }
}

