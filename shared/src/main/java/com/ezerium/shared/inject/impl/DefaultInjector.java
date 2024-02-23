package com.ezerium.shared.inject.impl;

import com.ezerium.shared.annotations.Inject;
import com.ezerium.shared.inject.Injector;
import com.ezerium.shared.logger.EzLogger;

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
