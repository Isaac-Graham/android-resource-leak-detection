package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis;

import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.Chain;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/28 21:57
 */
public class ICFGContext {
    private static final Logger logger = Logger.getLogger(ICFGContext.class);
    public static JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG();
    private static final HashMap<String, SootMethod> unitBodyMap = new HashMap<>();
    private static final HashMap<SootMethod, ExceptionalUnitGraph> cfgGraphs = new HashMap<>();
    private static final HashMap<SootMethod, Set<Loop>> methodLoops = new HashMap<>();

    public static void addCFGFromUnit(Unit unit) {
        SootMethod sootMethod = getMethodFromUnit(unit);
        addCFGFromMethod(sootMethod);
    }

    public static SootMethod getMethodFromUnit(Unit unit) {
        if (unitBodyMap.containsKey(unit.toString())) {
            return unitBodyMap.get(unit.toString());
        } else {
            return icfg.getMethodOf(unit);
        }
    }

    public static void addCFGFromMethod(SootMethod sootMethod) {
        if (cfgGraphs.containsKey(sootMethod)) return;
        cfgGraphs.put(sootMethod, new ExceptionalUnitGraph(sootMethod.getActiveBody()));
    }

    public static void addLoopFromUnit(Unit unit) {
        SootMethod sootMethod = getMethodFromUnit(unit);
        addLoopFromMethod(sootMethod);
    }

    public static void addLoopFromMethod(SootMethod sootMethod) {
        if (methodLoops.containsKey(sootMethod)) return;
        methodLoops.put(sootMethod, new LoopFinder().getLoops(sootMethod.getActiveBody()));
    }

    public static ExceptionalUnitGraph getCFGFromUnit(Unit unit) {
        SootMethod sootMethod = getMethodFromUnit(unit);
        return getCFGFromMethod(sootMethod);
    }

    public static ExceptionalUnitGraph getCFGFromMethod(SootMethod sootMethod) {
        if (!cfgGraphs.containsKey(sootMethod)) {
            addCFGFromMethod(sootMethod);
        }
        return cfgGraphs.get(sootMethod);
    }

    public static Set<Loop> getLoopsFromUnit(Unit unit) {
        SootMethod sootMethod = getMethodFromUnit(unit);
        return getLoopsFromMethod(sootMethod);
    }

    public static Set<Loop> getLoopsFromMethod(SootMethod sootMethod) {
        if (!methodLoops.containsKey(sootMethod)) {
            addLoopFromMethod(sootMethod);
        }
        return methodLoops.get(sootMethod);
    }

    public static void initializeUnitBodyMap(Chain<SootClass> sootClasses) {
        sootClasses.forEach(sootClass -> {
            sootClass.getMethods().forEach(sootMethod -> {
                SootMethodUtil.ensureSSA(sootMethod);
                SootMethodUtil.updateLocalName(sootMethod);
                Body body = sootMethod.getActiveBody();
                body.getUnits().forEach(unit -> {
                    unitBodyMap.put(unit.toString(), sootMethod);
                });
            });
        });
    }
}
