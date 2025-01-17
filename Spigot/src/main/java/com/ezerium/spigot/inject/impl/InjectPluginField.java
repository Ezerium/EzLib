package com.ezerium.spigot.inject.impl;

import com.ezerium.inject.Injector;
import com.ezerium.spigot.Spigot;

import java.lang.reflect.Field;

public class InjectPluginField implements Injector {

    @Override
    public boolean inject(Object object) {
        if (!(object instanceof Field))
            return false;

        Field field = (Field) object;
        field.setAccessible(true);

        switch (field.getType().getSimpleName()) {
            case "IDatabase":
                try {
                    field.set(object, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case "Config":
                try {
                    field.set(object, Spigot.INSTANCE.getConfig());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            default:
                break;
        }

        field.setAccessible(false);
        return false;
    }
}
