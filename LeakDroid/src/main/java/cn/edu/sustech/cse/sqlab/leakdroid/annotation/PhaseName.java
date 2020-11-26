package cn.edu.sustech.cse.sqlab.leakdroid.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Isaac Chen
 * @email ccccym666@gmail.com
 * @date 2020/11/26 20:27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PhaseName {
    String name();
}
