package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.pathextractor;

import soot.Unit;

import java.util.Objects;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/4/14 3:47
 */
public class Edge {
    private final Unit from;
    private final Unit to;

    public Edge(Unit from, Unit to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", from, to);
    }

    @Override
    public int hashCode() {
        return from.hashCode() * 13 + (to == null ? 0 : to.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Edge) {
            Edge edge = (Edge) o;
            if (!Objects.equals(from, edge.from)) return false;
            if (!Objects.equals(to, edge.to)) return false;
            return true;
        }
        return false;
    }
}
