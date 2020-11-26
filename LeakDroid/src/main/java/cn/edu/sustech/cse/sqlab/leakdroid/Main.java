package cn.edu.sustech.cse.sqlab.leakdroid;

import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.CommandOptions;
import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsParser;
import org.apache.log4j.Logger;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        OptionsParser.parse(args);
        CommandOptions.initialOptions();
    }
}

