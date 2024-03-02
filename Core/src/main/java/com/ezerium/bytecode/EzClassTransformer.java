package com.ezerium.bytecode;

import com.ezerium.annotations.TransformByAnnotation;
import de.icongmbh.oss.maven.plugin.javassist.ClassTransformer;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.build.JavassistBuildException;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public abstract class EzClassTransformer extends ClassTransformer {

    public Class<? extends Annotation> getAnnotation() {
        if (this.getClass().isAnnotationPresent(TransformByAnnotation.class)) throw new RuntimeException("Class " + this.getClass().getName() + " does not have an annotation set.");
        return this.getClass().getAnnotation(TransformByAnnotation.class).getClass();
    }

    public boolean ignore(CtClass ctClass) {
        return false;
    }

    public boolean ignore(CtMethod ctMethod) {
        return false;
    }

    public boolean ignore(CtField ctField) {
        return false;
    }

    public TransformTarget getTarget() {
        return TransformTarget.CLASS;
    }

    @Override
    public final boolean shouldTransform(CtClass ctClass) throws JavassistBuildException {
        if (this.ignore(ctClass)) return false;

        switch (this.getTarget()) {
            case CLASS:
                return ctClass.hasAnnotation(this.getAnnotation());
            case METHOD:
                return Arrays.stream(ctClass.getDeclaredMethods()).anyMatch(method -> method.hasAnnotation(this.getAnnotation()));
            case FIELD:
                return Arrays.stream(ctClass.getDeclaredFields()).anyMatch(field -> field.hasAnnotation(this.getAnnotation()));
            default:
                return false;
        }
    }

    @Override
    public void applyTransformations(CtClass ctClass) throws JavassistBuildException {
        switch (this.getTarget()) {
            case CLASS:
                this.transformClass(ctClass);
                break;
            case METHOD:
                for (CtMethod method : ctClass.getDeclaredMethods()) {
                    if (method.hasAnnotation(this.getAnnotation())) {
                        if (this.ignore(method)) continue;

                        this.transformMethod(method);
                    }
                }
                break;
            case FIELD:
                for (CtField field : ctClass.getDeclaredFields()) {
                    if (field.hasAnnotation(this.getAnnotation())) {
                        if (this.ignore(field)) continue;

                        this.transformField(field);
                    }
                }
                break;
        }
    }

    public void transformMethod(CtMethod method) throws JavassistBuildException {

    }

    public void transformField(CtField field) throws JavassistBuildException {

    }

    public void transformClass(CtClass clazz) throws JavassistBuildException {

    }

    public enum TransformTarget {

        CLASS,
        FIELD,
        METHOD

    }

    public final boolean isVoid(CtMethod method) throws NotFoundException {
        return method.getReturnType().getName().equals("void");
    }
}
