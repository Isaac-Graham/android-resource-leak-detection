package cn.edu.sustech.cse.sqlab.leakdroid;

import org.apache.log4j.Logger;

import java.util.List;

import static java.util.Collections.emptyList;

public class Main {
    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {


        List<Integer> list = emptyList();
        logger.info(list.size() + "");
    }
}
