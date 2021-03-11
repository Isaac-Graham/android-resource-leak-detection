package cn.edu.sustech.cse.sqlab.leakdroid.tranformers;

import soot.Body;
import soot.SootMethod;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/28 21:57
 */
public class ICFGContext {
    public static JimpleBasedInterproceduralCFG icfg;
    public static HashMap<Body, ExceptionalUnitGraph> cfgGraphs;
    public static HashMap<Body, Set<Loop>> bodyLoops;

//    public static
}
