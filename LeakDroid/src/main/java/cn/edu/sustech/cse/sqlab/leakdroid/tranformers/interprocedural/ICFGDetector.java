package cn.edu.sustech.cse.sqlab.leakdroid.tranformers.interprocedural;

import cn.edu.sustech.cse.sqlab.leakdroid.annotation.PhaseName;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.tags.ResourceLeakTag;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.AbstractSpecialInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.RValueBox;
import soot.shimple.PhiExpr;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/2/18 22:19
 */
@PhaseName(name = "stp.testicfg")
public class ICFGDetector extends BodyTransformer {
    private final static Logger logger = Logger.getLogger(ICFGDetector.class);

    public class DefUseUtils {
        List<Unit> defUnits;
        List<Unit> useUnits;

        DefUseUtils(List<Unit> defUnits, List<Unit> useUnits) {
            this.defUnits = defUnits;
            this.useUnits = useUnits;
        }
    }

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        SootMethodUtil.ensureSSA(body.getMethod());
        SootMethodUtil.updateLocalName(body.getMethod());
        ExceptionalUnitGraph cfg = new ExceptionalUnitGraph(body);

//        if (body.getMethod().toString().contains(SootMethod.staticInitializerName)) return;
//        ExceptionalUnitGraph cfg = new ExceptionalUnitGraph(body);
//        body.getUnits().stream().filter(Analyzer::isRequest).forEach(unit -> {
//            if (Analyzer.isLeakage(unit, cfg)) {
//                logger.info(body.getMethod());
//            }
//        });
        if (!body.getMethod().getName().contains("foo")) return;

//        SimpleLocalDefs slf = new SimpleLocalDefs(cfg);
//        body.getLocals().forEach(local-> {
//            logger.info(slf.getDefsOf(local));
//        });


        // 生成Local: DefUnitList的HashMap
        SimpleLocalDefs sld = new SimpleLocalDefs(cfg);
        SimpleLocalUses slu = new SimpleLocalUses(body, sld);
        HashMap<Value, List<Unit>> localDefHashMap = new HashMap<>();
        body.getLocals().forEach(local -> {
            if (sld.getDefsOf(local).stream().anyMatch(unit -> !(unit instanceof DefinitionStmt))) {
                logger.error(String.format("Error occurs: some not DefinitionStmt in def list: %s", sld.getDefsOf(local)));
            }
            localDefHashMap.put(local, sld.getDefsOf(local));
        });

        Set<Value> locals = new HashSet<>();
        int lastSize = 0;
        locals.add(getFirstLocal(body));
        while (locals.size() > lastSize) {
            lastSize = locals.size();
//            List<Local> localList = Lists.newArrayList(locals);
            localDefHashMap.forEach((local, defList) -> {
                if (locals.contains(local)) return;
                defList.forEach(def -> {
                    DefinitionStmt definitionStmt = (DefinitionStmt) def;
                    if (locals.contains(definitionStmt.getRightOp())) {
                        locals.add(definitionStmt.getLeftOp());
                    } else if (definitionStmt.getRightOp() instanceof PhiExpr) {
                        PhiExpr phiExpr = (PhiExpr) definitionStmt.getRightOp();
                        phiExpr.getValues().forEach(value -> {
                            if (locals.contains(value)) {
                                locals.add(definitionStmt.getLeftOp());
                            }
                        });
                    }
                });
            });
        }
        logger.info(locals);
    }


    private static Local getFirstLocal(Body body) {
        ExceptionalUnitGraph cfg = new ExceptionalUnitGraph(body);
        SimpleLocalDefs sld = new SimpleLocalDefs(cfg);
        Unit res = null;

        List<Unit> units = body.getUnits()
                .stream()
                .filter(Analyzer::isRequest)
                .collect(Collectors.toList());
        for (Unit unit : units) {
            AbstractSpecialInvokeExpr iie = (AbstractSpecialInvokeExpr) (((JInvokeStmt) unit).getInvokeExpr());
            for (Local local : body.getLocals()) {
                if (iie.getBase().toString().equals(local.getName())) {
                    return local;
                }
            }
        }
        return null;
    }
}
