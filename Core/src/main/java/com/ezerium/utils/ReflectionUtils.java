package com.ezerium.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

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

    public static Collection<Class<?>> getClassesInPackage(Class<?> mainClass, String packageName) {
        try {
            CodeSource codeSource = mainClass.getProtectionDomain().getCodeSource();
            if (codeSource == null) return null;

            String mainClassPath = mainClass.getProtectionDomain().getCodeSource().getLocation().getPath();
            JarFile jarFile = new JarFile(mainClassPath);
            return jarFile.stream()
                    .filter(jarEntry -> jarEntry.getName().startsWith(packageName) && jarEntry.getName().endsWith(".class"))
                    .map(jarEntry -> {
                        String className = jarEntry.getName().replace("/", ".").replace(".class", "");
                        try {
                            return Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            return null;
                        }
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}
