package cn.edu.sustech.cse.sqlab.leakdroid;

import cn.edu.sustech.cse.sqlab.leakdroid.runSoot.SootAnalyzer;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.UnloadableBodiesEliminator;
import org.apache.log4j.Logger;

import java.util.List;

import static java.util.Collections.emptyList;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        SootAnalyzer analyzer = new SootAnalyzer();
        analyzer.run();
    }
}

