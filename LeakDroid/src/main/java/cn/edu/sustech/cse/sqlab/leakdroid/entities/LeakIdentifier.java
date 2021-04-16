package cn.edu.sustech.cse.sqlab.leakdroid.entities;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/4/15 20:43
 */
public enum LeakIdentifier {
    LEAK, NOT_LEAK, UN_KNOWN, NO_RESOURCES;

    public static LeakIdentifier max(LeakIdentifier l1, LeakIdentifier l2) {
        if (l1 == LEAK || l2 == LEAK) {
            return LEAK;
        } else if (l1 == UN_KNOWN || l2 == UN_KNOWN) {
            return UN_KNOWN;
        }
        return NOT_LEAK;
    }
}
