package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities;

import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;
import com.google.common.collect.Lists;
import soot.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/9 14:50
 */
public class CFGPath implements Cloneable {
    private List<Unit> path;

    public CFGPath() {
        this.path = new ArrayList<>();
    }

    @Override
    public Object clone() {
        CFGPath cfgPath = null;
        try {
            cfgPath = (CFGPath) super.clone();
            cfgPath.path = new ArrayList<>(path);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cfgPath;
    }

    public boolean isEnd() {
        return ICFGContext.icfg.isExitStmt(path.get(path.size() - 1));
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    public boolean equals(CFGPath cfgPath) {
        List<Unit> anotherPath = cfgPath.getPath();
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

    public void mergeCFGPath(CFGPath cfgPath) {
        this.path.addAll(cfgPath.path);
    }

    public String toString() {
        return path.toString();
    }
}
