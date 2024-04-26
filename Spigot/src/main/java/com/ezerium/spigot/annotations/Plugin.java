package com.ezerium.spigot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Plugin {

    String name();
    String version();
    String apiVersion() default "";
    String description() default "";
    String[] authors() default {};
    String[] depend() default {};
    String[] softDepend() default {};
    String[] loadBefore() default {};
    String[] loadAfter() default {};

}
