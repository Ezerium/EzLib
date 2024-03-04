package com.ezerium.annotations;

import com.ezerium.logger.debug.DebugAt;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Debug {
    String logOnCall();

    DebugAt debugAt() default DebugAt.START;

}
