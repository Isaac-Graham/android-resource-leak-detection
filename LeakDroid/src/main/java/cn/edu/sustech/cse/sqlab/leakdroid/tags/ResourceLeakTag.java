package cn.edu.sustech.cse.sqlab.leakdroid.tags;

import cn.edu.sustech.cse.sqlab.leakdroid.exceptions.NonImplementException;
import soot.Unit;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/1 21:03
 */
public class ResourceLeakTag implements Tag {

    public static final String name = "tag.stmt.resource_leak";
    private final List<Unit> successors = new ArrayList<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        throw new NonImplementException("Has not been implemented");
    }

    public void addSuccessor(Unit unit) {
        this.successors.add(unit);
    }

    public List<Unit> getSuccessors() {
        return successors;
    }
}
