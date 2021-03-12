package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath;

import org.apache.log4j.Logger;
import soot.jimple.toolkits.annotation.logic.Loop;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/11 16:22
 */
public class LoopOnceCFGPath extends BaseCFGPath implements Cloneable {
    private static final Logger logger = Logger.getLogger(LoopOnceCFGPath.class);
    private Loop currentLoop;

    public LoopOnceCFGPath(Loop currentLoop) {
        this.currentLoop = currentLoop;
    }

    @Override
    public Object clone() {
        return (LoopOnceCFGPath) super.clone();
    }

    @Override
    public boolean isEnd() {
        return currentLoop.getBackJumpStmt() == this.getPathTail();
    }

}
