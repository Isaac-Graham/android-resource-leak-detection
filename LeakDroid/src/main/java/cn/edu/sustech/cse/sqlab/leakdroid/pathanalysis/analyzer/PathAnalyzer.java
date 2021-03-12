package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.analyzer;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath.BaseCFGPath;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import org.apache.log4j.Logger;
import soot.Body;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.internal.AbstractSpecialInvokeExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;

import java.util.*;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:49
 */
public class PathAnalyzer {
    private static final Logger logger = Logger.getLogger(PathAnalyzer.class);
    private final UnitGraph cfg;
    private final HashMap<Value, List<Unit>> localDefHashMap;
    private final List<BaseCFGPath> paths;

    public PathAnalyzer(Body body, List<BaseCFGPath> paths) {
        this.cfg = new ExceptionalUnitGraph(body);
        this.localDefHashMap = new HashMap<>();
        this.paths = paths;
        initialLocalDefHashMap(body);
    }

    public void analyze() {
        paths.forEach(this::analyze);
    }

    private void initialLocalDefHashMap(Body body) {
        SimpleLocalDefs sld = new SimpleLocalDefs(this.cfg);
        SimpleLocalUses slu = new SimpleLocalUses(body, sld);
        body.getLocals().forEach(local -> {
            if (sld.getDefsOf(local).stream().anyMatch(unit -> !(unit instanceof DefinitionStmt))) {
                logger.error(String.format("Error occurs: All should be DefinitionStmt in def list: %s", sld.getDefsOf(local)));
            }
            this.localDefHashMap.put(local, sld.getDefsOf(local));
        });
    }

    private static Value getFirstVariable(InvokeStmt startInvokeStmt) {
        AbstractSpecialInvokeExpr abstractStartSpecialInvokeExpr = (AbstractSpecialInvokeExpr) startInvokeStmt.getInvokeExpr();
        return abstractStartSpecialInvokeExpr.getBase();
    }


    private void analyze(BaseCFGPath cfgPath) {
        Set<Value> localValuables = new HashSet<>();
        List<Unit> path = cfgPath.getPath();
        if (path.isEmpty()) return;
        localValuables.add(getFirstVariable((InvokeStmt) path.get(0)));
        for (int i = 1; i < path.size(); i++) {
            Unit nextUnit = path.get(i);
            if (ResourceUtil.isRelease(nextUnit)) {
                return;
            }
            Value localValueThisUnit = getLocalValueFromDefinitions(nextUnit);
            if (localValueThisUnit != null) {
                localValuables.add(localValueThisUnit);
            }
        }
        PathAnalyzer.reportStackUnitInfo(path);
    }

    private Value getLocalValueFromDefinitions(Unit nextUnit) {
        Set<Map.Entry<Value, List<Unit>>> entrySet = this.localDefHashMap.entrySet();
        for (Map.Entry<Value, List<Unit>> valueListEntry : entrySet) {
            for (Unit def : valueListEntry.getValue()) {
                if (def == nextUnit) {
                    return ((DefinitionStmt) def).getLeftOp();
                }
            }
        }
        return null;
    }

    private static void reportStackUnitInfo(List<Unit> path) {
        StringBuilder res = new StringBuilder();
        path.forEach(ele -> {
            ele.addTag(new ResourceLeakTag());
            res.append(ele).append(" -> ");
        });
        res.append("END");
        logger.info(String.format("Resource leak path: %s", res));
    }
}
