package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath;

import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.ICFGContext;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:21
 */
public class CFGPath extends BaseCFGPath implements Cloneable {
    public CFGPath() {
        super();
    }

    @Override
    public Object clone() {
        return (CFGPath) super.clone();
    }

    @Override
    public boolean isEnd() {
        return ICFGContext.icfg.isExitStmt(this.getPathTail());
    }
}
