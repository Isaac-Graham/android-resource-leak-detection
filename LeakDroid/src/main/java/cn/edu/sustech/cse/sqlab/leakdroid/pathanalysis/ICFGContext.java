package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis;

import cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier;
import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.utils.InterProcedureUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.UnitMethodNameTag;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.Chain;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.edu.sustech.cse.sqlab.leakdroid.entities.LeakIdentifier.NOT_LEAK;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/28 21:57
 */
public class ICFGContext {
    private static final Logger logger = Logger.getLogger(ICFGContext.class);
    public static JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG();
    private static final HashMap<SootMethod, ExceptionalUnitGraph> cfgGraphs = new HashMap<>();
    private static final HashMap<SootMethod, Set<Loop>> methodLoops = new HashMap<>();
    public static final Set<SootMethod> processingMethods = new HashSet<>();
    public static final HashMap<SootMethod, List<LeakIdentifier>> methodArgsLeakCached = new HashMap<>();
    public static final HashMap<SootMethod, LeakIdentifier> methodLeakIdentify = new HashMap<>();

    public static void addCFGFromMethod(SootMethod sootMethod) {
        if (cfgGraphs.containsKey(sootMethod)) return;
        cfgGraphs.put(sootMethod, new ExceptionalUnitGraph(sootMethod.getActiveBody()));
    }

    public static void addLoopFromMethod(SootMethod sootMethod) {
        if (methodLoops.containsKey(sootMethod)) return;
        methodLoops.put(sootMethod, new LoopFinder().getLoops(sootMethod.getActiveBody()));
    }

    public static ExceptionalUnitGraph getCFGFromUnit(Unit unit) {
        SootMethod sootMethod = UnitUtil.getSootMethod(unit);
        if (sootMethod == null) return null;
        return getCFGFromMethod(sootMethod);
    }

    public static ExceptionalUnitGraph getCFGFromMethod(SootMethod sootMethod) {
        if (!cfgGraphs.containsKey(sootMethod)) {
            addCFGFromMethod(sootMethod);
        }
        return cfgGraphs.get(sootMethod);
    }

    public static Set<Loop> getLoopsFromUnit(Unit unit) {
        SootMethod sootMethod = UnitUtil.getSootMethod(unit);
        if (sootMethod == null) return null;
        return getLoopsFromMethod(sootMethod);
    }

    public static Set<Loop> getLoopsFromMethod(SootMethod sootMethod) {
        if (!methodLoops.containsKey(sootMethod)) {
            addLoopFromMethod(sootMethod);
        }
        return methodLoops.get(sootMethod);
    }

    public static LeakIdentifier getSootMethodArgLeakIdentifier(SootMethod invokeMethod, int argIndex) {
        if (!methodArgsLeakCached.containsKey(invokeMethod)) {
            List<LeakIdentifier> list = new ArrayList<>(Collections.nCopies(invokeMethod.getParameterCount(), null));
            methodArgsLeakCached.put(invokeMethod, list);
        }
        return methodArgsLeakCached.get(invokeMethod).get(argIndex);

    }

    public static void setSootMethodArgLeakIdentifier(SootMethod invokeMethod, int argIndex, LeakIdentifier identifier) {
        methodArgsLeakCached.get(invokeMethod).set(argIndex, identifier);
    }

    public static LeakIdentifier getMethodLeakIdentifier(SootMethod method) {
        if (!methodLeakIdentify.containsKey(method)) {
            logger.warn(String.format("Fail to get leak result of method: %s", SootMethodUtil.getFullName(method)));
            return null;
        }
        return methodLeakIdentify.get(method);
    }

    public static void SetMethodLeakIdentifier(SootMethod method, LeakIdentifier identifier) {
        methodLeakIdentify.put(method, identifier);
    }
}
