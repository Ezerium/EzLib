package com.ezerium.shared.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

@UtilityClass
public class ReflectionUtils {

    @Nullable
    public static Object getFieldValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            boolean isAccessible = field.isAccessible();
            if (!isAccessible) field.setAccessible(true);

            Object value = field.get(object);
            if (!isAccessible) field.setAccessible(false);

            return value;
        } catch (Exception e) {
            return null;
        }
    }

}
