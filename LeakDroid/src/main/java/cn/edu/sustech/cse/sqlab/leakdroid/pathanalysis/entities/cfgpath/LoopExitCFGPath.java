package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath;

import soot.jimple.toolkits.annotation.logic.Loop;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 17:11
 */
public class LoopExitCFGPath extends BaseCFGPath implements Cloneable {
    private Loop currentLoop;

    public LoopExitCFGPath(Loop currentLoop) {
        this.currentLoop = currentLoop;
    }

    @Override
    public Object clone() {
        return (LoopExitCFGPath) super.clone();
    }

    @Override
    public boolean isEnd() {
        return currentLoop.getLoopExits().contains(this.getPathTail());
    }
}
