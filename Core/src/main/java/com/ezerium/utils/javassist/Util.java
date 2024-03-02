package com.ezerium.utils.javassist;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;

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

}
