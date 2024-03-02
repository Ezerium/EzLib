package com.ezerium.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransformByAnnotation {

    Class<? extends Annotation>[] value();

}
