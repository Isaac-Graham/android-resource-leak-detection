package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.pathstatus;

import soot.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 19:02
 */
public abstract class BasePathStatus implements Cloneable {
    public Stack<Unit> pathStack;
    public Stack<Stack<Unit>> neighborStack;

    public BasePathStatus() {
        this.pathStack = new Stack<>();
        this.neighborStack = new Stack<>();
    }

    @Override
    public Object clone() {
        BasePathStatus basePathStatus = null;
        try {
            basePathStatus = (BasePathStatus) super.clone();
            basePathStatus.pathStack = (Stack<Unit>) pathStack.clone();
            basePathStatus.neighborStack = (Stack<Stack<Unit>>) neighborStack.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return basePathStatus;
    }

    public boolean isEnd() {
        return pathStack.empty();
    }

    public Stack<Unit> getNeighborTop() {
        return neighborStack.peek();
    }

    public abstract void addPath(Unit unit);

    public void callBack() {
        this.pathStack.pop();
        this.neighborStack.pop();
    }

    public void mergePathStatus(BasePathStatus basePathStatus) {
        List<Unit> anotherPathStack = new ArrayList<>(basePathStatus.pathStack);
        List<Stack<Unit>> anotherNeighborStack = new ArrayList<>(basePathStatus.neighborStack);
        anotherPathStack.forEach(unit -> this.pathStack.push(unit));
        anotherNeighborStack.forEach(unitStack -> this.neighborStack.push(unitStack));
    }

    public String toString() {
        return String.format("[%s]_[%s]", this.pathStack.toString(), this.neighborStack.toString());
    }

    public void clearNeighborStack() {
        int size = neighborStack.size();
        this.neighborStack = new Stack<>();
        for (int i = 0; i < size; i++) {
            neighborStack.push(new Stack<>());
        }
    }
}
