package cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.entities.cfgpath;

import cn.edu.sustech.cse.sqlab.leakdroid.pathanalysis.ICFGContext;
import org.apache.log4j.Logger;
import soot.toolkits.graph.ExceptionalUnitGraph;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/12 15:21
 */
public class CFGPath extends BaseCFGPath implements Cloneable {
    private final static Logger logger = Logger.getLogger(CFGPath.class);

    public CFGPath() {
        super();
    }

    @Override
    public Object clone() {
        return (CFGPath) super.clone();
    }

    @Override
    public boolean isEnd() {
        ExceptionalUnitGraph unitGraph = ICFGContext.getCFGFromUnit(this.getPathTail());
        if (unitGraph == null) {
            logger.warn("CFG is null");
            return true;
        }
        return unitGraph.getTails().contains(this.getPathTail());
    }
}
