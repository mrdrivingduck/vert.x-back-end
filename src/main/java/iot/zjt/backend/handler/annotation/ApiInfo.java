package iot.zjt.backend.handler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describe the API information of a handler.
 * 
 * @author Mr Dk.
 * @since 2020/03/13
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiInfo {
    String url() default "";
    String version() default "1.0.0";
}