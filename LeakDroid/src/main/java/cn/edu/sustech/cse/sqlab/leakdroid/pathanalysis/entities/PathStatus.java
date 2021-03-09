package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities;

import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import soot.Unit;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/9 14:50
 */
public class PathStatus implements Cloneable {
    public Stack<Unit> pathStack;
    public Stack<Stack<Unit>> neighborStack;

    public PathStatus() {
        this.pathStack = new Stack<>();
        this.neighborStack = new Stack<>();
    }

    @Override
    public Object clone() {
        PathStatus pathStatus = null;
        try {
            pathStatus = (PathStatus) super.clone();
            pathStatus.pathStack = (Stack<Unit>) pathStack.clone();
            pathStatus.neighborStack = (Stack<Stack<Unit>>) neighborStack.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return pathStatus;
    }

    public boolean isEnd() {
        return pathStack.empty();
    }

    public Stack<Unit> getNeighborTop() {
        return neighborStack.peek();
    }

    public void addPath(Unit unit) {
//        if (unit == null) {
//            throw new IllegalArgumentException("Error occurs: Unit being null");
//        }
        this.pathStack.add(unit);
        if (unit == null) {
            this.neighborStack.add(new Stack<>());
        } else {
            Stack<Unit> successors = new Stack<>();
            ExceptionalUnitGraph cfg = ICFGContext.cfgGraphs.get(ICFGContext.icfg.getBodyOf(unit));
            cfg.getSuccsOf(unit).forEach(successors::push);
            this.neighborStack.push(successors);
        }
    }

    public void callBack() {
        this.pathStack.pop();
        this.neighborStack.pop();
    }

    public void mergePathStatus(PathStatus pathStatus) {
        List<Unit> anotherPathStack = new ArrayList<>(pathStatus.pathStack);
        List<Stack<Unit>> anotherNeighborStack = new ArrayList<>(pathStatus.neighborStack);
        anotherPathStack.forEach(unit -> this.pathStack.push(unit));
        anotherNeighborStack.forEach(unitStack -> this.neighborStack.push(unitStack));
    }

    public String toString() {
        return String.format("[%s]_[%s]", this.pathStack.toString(), this.neighborStack.toString());
    }
}
