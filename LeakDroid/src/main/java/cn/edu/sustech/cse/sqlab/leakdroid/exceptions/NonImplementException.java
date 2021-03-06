package cn.edu.sustech.cse.sqlab.leakdroid.exceptions;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2021/3/1 21:53
 */
public class NonImplementException extends RuntimeException {
    public NonImplementException() {
        super();
    }

    public NonImplementException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonImplementException(String message) {
        super(message);
    }

    public NonImplementException(Throwable cause) {
        super(cause);
    }
}
