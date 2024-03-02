package com.ezerium.inject.impl;

import com.ezerium.inject.Injector;
import com.ezerium.logger.EzLogger;
import com.ezerium.annotations.Inject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class DefaultInjector implements Injector {

    @Override
    public boolean inject(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                try {
                    field.set(object, field.getType().getDeclaredConstructor().newInstance());
                } catch (IllegalAccessException | NoSuchMethodException | InstantiationException |
                         InvocationTargetException e) {
                    EzLogger.logWarning("Failed to inject field " + field.getName() + " in " + object.getClass().getSimpleName() + ". Perhaps the field requires a custom injector?");
                }
            }
        }

        return true;
    }
}
