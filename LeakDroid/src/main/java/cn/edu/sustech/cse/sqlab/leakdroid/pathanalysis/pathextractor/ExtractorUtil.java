package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.pathextractor;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import cn.edu.sustech.cse.sqlab.leakdroid.util.ResourceUtil;
import cn.edu.sustech.cse.sqlab.leakdroid.util.UnitUtil;
import soot.Unit;
import soot.jimple.InvokeStmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/4/13 19:30
 */
public class ExtractorUtil {
    Stack<List<Unit>> successorsStack;
    Stack<Unit> dfsStack;
    List<Edge> edges;
    Set<Loop> loops;
    final int defaultMaxMeetTimes = 1;
    final int defaultLoopMaxMeetTimes = 2;

    ExtractorUtil(Unit startUnit) {
        dfsStack = new Stack<>();
        successorsStack = new Stack<>();
        meetUnit(startUnit);
        loops = ICFGContext.getLoopsFromUnit(startUnit);
        edges = new ArrayList<>();
    }

    List<Unit> getSuccessors() {
        Unit topUnit = dfsStack.peek();
        return successorsStack.peek()
                .stream()
                .filter(successor -> this.validSuccessor(topUnit, successor))
                .collect(Collectors.toList());
    }

    void meetUnit(Unit unit) {
        if (!dfsStack.empty() && !successorsStack.empty()) {
            Unit topUnit = dfsStack.peek();
            edges.add(new Edge(topUnit, unit));
            successorsStack.peek().remove(unit);
        }
        dfsStack.push(unit);
        successorsStack.push(getSuccessors(unit));
    }

    void callBack() {
        Unit poppedUnit = dfsStack.pop();
        successorsStack.pop();
        if (!dfsStack.empty()) {
            Edge edge = new Edge(dfsStack.peek(), poppedUnit);
            edges.remove(edge);
        }
    }

    private List<Unit> getSuccessors(Unit topUnit) {
        List<Unit> successors = new ArrayList<>();
        ExceptionalUnitGraph cfg = ICFGContext.getCFGFromUnit(topUnit);
        if (cfg != null) {
            cfg.getSuccsOf(topUnit).forEach(successor -> {
                if (ResourceUtil.isRequest(topUnit) && UnitUtil.isCaughtExceptionRef(successor)) return;
                if (!(topUnit instanceof InvokeStmt) && UnitUtil.isCaughtExceptionRef(successor)) return;
                successors.add(successor);
            });
        }
        return successors;
    }


    private boolean validSuccessor(Unit from, Unit to) {
        Edge curEdge = new Edge(from, to);
        List<Edge> matchedEdges = edges.stream()
                .filter(edge -> edge.equals(curEdge))
                .collect(Collectors.toList());
        if (isLoopStmt(from) && isLoopStmt(to)) {
            return matchedEdges.size() < defaultLoopMaxMeetTimes;
        } else {
            return matchedEdges.size() < defaultMaxMeetTimes;
        }
    }


    private boolean isLoopStmt(Unit unit) {
        for (Loop loop : loops) {
            if (loop.getLoopStatements().contains(unit)) return true;
        }
        return false;
    }
}