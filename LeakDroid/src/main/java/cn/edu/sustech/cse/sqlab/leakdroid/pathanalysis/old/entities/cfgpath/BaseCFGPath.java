package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.old.entities.cfgpath;

import soot.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 18:50
 */
@Deprecated
public abstract class BaseCFGPath implements Cloneable {
    protected List<Unit> path;

    public BaseCFGPath() {
        this.path = new ArrayList<>();
    }

    @Override
    public Object clone() {
        BaseCFGPath baseCFGPath = null;
        try {
            baseCFGPath = (BaseCFGPath) super.clone();
            baseCFGPath.path = new ArrayList<>(path);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return baseCFGPath;
    }

    public abstract boolean isEnd();

    public boolean isEmpty() {
        return path.isEmpty();
    }

    public boolean equals(BaseCFGPath baseCFGPath) {
        List<Unit> anotherPath = baseCFGPath.getPath();
        if (anotherPath.size() != path.size()) {
            return false;
        }

        for (int i = 0; i < path.size(); i++) {
            if (path.get(i) != anotherPath.get(i)) {
                return false;
            }
        }
        return true;
    }

    public List<Unit> getPath() {
        return path;
    }

    public int getLength() {
        return this.path.size();
    }

    public void addPath(Unit nextUnit) {
        this.path.add(nextUnit);
    }

    public void callBack() {
        this.path.remove(this.path.size() - 1);
    }

    public Unit getPathNode(int index) {
        return path.get(index);
    }

    public void mergeCFGPath(BaseCFGPath baseCFGPath) {
        this.path.addAll(baseCFGPath.path);
    }

    public String toString() {
        return path.toString();
    }

    public Unit getPathTail() {
        return path.get(path.size() - 1);
    }
}
