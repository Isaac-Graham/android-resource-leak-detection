package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.pathextractor;

import soot.Unit;

import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/4/13 19:25
 */
public class CFGPath {
    final List<Unit> path;

    public CFGPath(List<Unit> path) {
        this.path = path;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        path.forEach(n -> {
            res.append(n).append(" -> ");
        });
        return res.toString();
    }

    public List<Unit> getPath() {
        return path;
    }
}
