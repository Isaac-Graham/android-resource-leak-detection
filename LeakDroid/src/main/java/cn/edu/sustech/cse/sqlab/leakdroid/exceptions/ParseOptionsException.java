package cn.edu.sustech.cse.sqlab.leakdroid.exceptions;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2020/11/26 23:51
 */
public class ParseOptionsException extends RuntimeException {
    public ParseOptionsException() {
        super();
    }

    public ParseOptionsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseOptionsException(String message) {
        super(message);
    }

    public ParseOptionsException(Throwable cause) {
        super(cause);
    }
}
