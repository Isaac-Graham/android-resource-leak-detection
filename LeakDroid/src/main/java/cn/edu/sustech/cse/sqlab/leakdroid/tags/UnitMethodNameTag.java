package cn.edu.sustech.cse.sqlab.leakdroid.tags;

import cn.edu.sustech.cse.sqlab.leakdroid.exceptions.NonImplementException;
import cn.edu.sustech.cse.sqlab.leakdroid.util.SootMethodUtil;
import soot.SootMethod;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/16 23:29
 */
public class UnitMethodNameTag implements Tag {
    private final SootMethod sootMethod;

    public UnitMethodNameTag(SootMethod sootMethod) {
        this.sootMethod = sootMethod;
    }

    @Override
    public String getName() {
        return SootMethodUtil.getFullName(sootMethod);
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        throw new NonImplementException("Has not been implemented");
    }

    public String toString() {
        return this.getName();
    }

    public SootMethod getSootMethod() {
        return sootMethod;
    }
}
