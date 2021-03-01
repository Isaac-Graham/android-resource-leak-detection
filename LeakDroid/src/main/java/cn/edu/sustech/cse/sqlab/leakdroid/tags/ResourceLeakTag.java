package cn.edu.sustech.cse.sqlab.leakdroid.tags;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/1 21:03
 */
public class ResourceLeakTag implements Tag {

    public static final String name = "tag.stmt.resource_leak";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return null;
//        throw AttributeValueException
    }
}
