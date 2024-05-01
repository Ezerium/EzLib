package com.ezerium.jda.annotations;

import net.dv8tion.jda.api.events.GenericEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {

    Class<? extends GenericEvent> value();

}
