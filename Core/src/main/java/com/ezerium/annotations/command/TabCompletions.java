package com.ezerium.annotations.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TabCompletions {

    TabCompleter[] value();

    @interface TabCompleter {
        int index();
        String[] values() default {};
        String methodRef() default "";
    }
}