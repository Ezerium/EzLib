package com.ezerium.utils.javassist;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.Opcode;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Util {

    public static void deleteAnnotationIfPresent(CtClass ctClass, Class<?> annotation) {
        if (ctClass.hasAnnotation(annotation)) {
            AnnotationsAttribute attr = getAnnotationsAttribute(ctClass);
            attr.removeAnnotation(annotation.getName());
        }
    }

    public static void deleteAnnotationIfPresent(CtMethod ctMethod, Class<?> annotation) {
        if (ctMethod.hasAnnotation(annotation)) {
            AnnotationsAttribute attr = getAnnotationsAttribute(ctMethod);
            attr.removeAnnotation(annotation.getName());
        }
    }

    public static void deleteAnnotationIfPresent(CtField ctField, Class<?> annotation) {
        if (ctField.hasAnnotation(annotation)) {
            AnnotationsAttribute attr = getAnnotationsAttribute(ctField);
            attr.removeAnnotation(annotation.getName());
        }
    }

    public static AnnotationsAttribute getAnnotationsAttribute(CtClass ctClass) {
        return (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
    }

    public static AnnotationsAttribute getAnnotationsAttribute(CtMethod ctMethod) {
        return (AnnotationsAttribute) ctMethod.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
    }

    public static AnnotationsAttribute getAnnotationsAttribute(CtField ctField) {
        return (AnnotationsAttribute) ctField.getFieldInfo().getAttribute(AnnotationsAttribute.visibleTag);
    }

    public static void runAsync(String methodName, Object instance, Object... args) {
        CompletableFuture.runAsync(() -> {
            try {
                Method method = instance.getClass().getDeclaredMethod(methodName);
                method.invoke(instance, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Object runAsyncWithReturn(String methodName, Object instance, Object... args) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    Method method = instance.getClass().getDeclaredMethod(methodName);
                    return method.invoke(instance, args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static int findLastAssignmentOrVariable(CtMethod method, String variableName) {
        /*CodeIterator iterator = method.getMethodInfo().getCodeAttribute().iterator();
        while (iterator.hasNext()) {
            int index = iterator.next();
            int op = iterator.byteAt(index);
            if (op == Opcode.ASTORE) {
                int localVarIndex = iterator.byteAt(index + 1);
                if (localVarIndex == method.getMethodInfo().getCodeAttribute().getLocalVariableAttribute().nameIndex(variableName)) {
                    return index;
                }
            }
        }*/
        return -1;
    }
}
