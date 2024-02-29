package com.ezerium.shared.annotations;

import com.ezerium.shared.logger.debug.DebugAt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Debug {
    String logOnCall();

    DebugAt debugAt() default DebugAt.START;

}
