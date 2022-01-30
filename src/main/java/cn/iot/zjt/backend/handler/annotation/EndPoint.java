package cn.iot.zjt.backend.handler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to describe an API end point.
 *
 * @version 2021/10/13
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EndPoint {
  String path();
  String version() default "";
  String[] methods();
  boolean jwtAuth() default true;
  boolean block() default false;
}
