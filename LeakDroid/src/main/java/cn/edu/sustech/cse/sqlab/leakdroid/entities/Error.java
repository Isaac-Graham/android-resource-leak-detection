package cn.edu.sustech.cse.sqlab.leakdroid.entities;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/4/15 20:38
 */
public enum Error {
    PARSE_ERROR(-1);
    public int errorCode;

    Error(int code) {
        this.errorCode = code;
    }
}
