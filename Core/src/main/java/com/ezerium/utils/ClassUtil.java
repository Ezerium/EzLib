package com.ezerium.utils;

import com.google.common.collect.ImmutableList;
import lombok.experimental.UtilityClass;

import java.security.CodeSource;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@UtilityClass
public class ClassUtil {

    public static Collection<Class<?>> getClassesInPackage(Class<?> clazz, String packageName) {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            try {
                JarFile jarFile = new JarFile(codeSource.getLocation().toURI().getPath());
                return jarFile.stream()
                        .filter(jarEntry -> jarEntry.getName().startsWith(packageName) && jarEntry.getName().endsWith(".class"))
                        .map(jarEntry -> {
                            String className = jarEntry.getName().replace("/", ".").replace(".class", "");
                            try {
                                return Class.forName(className);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .collect(ImmutableList.toImmutableList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ImmutableList.of();
    }

}
