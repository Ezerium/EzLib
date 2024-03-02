package com.ezerium.bytecode.async;

import com.ezerium.annotations.Async;
import com.ezerium.annotations.command.Command;
import com.ezerium.bytecode.EzClassTransformer;
import com.ezerium.utils.javassist.Util;
import javassist.*;
import javassist.build.JavassistBuildException;
import javassist.bytecode.*;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;

import java.lang.annotation.Annotation;
import java.util.concurrent.CompletableFuture;

public class AsyncTransformer extends EzClassTransformer {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Async.class;
    }

    @Override
    public boolean ignore(CtMethod ctMethod) {
        return ctMethod.hasAnnotation(Command.class);
    }

    @Override
    public void transformMethod(CtMethod method) throws JavassistBuildException {
        try {
            Util.deleteAnnotationIfPresent(method, Async.class);

            CtClass ctClass = method.getDeclaringClass();

            CtMethod copy = new CtMethod(method, method.getDeclaringClass(), null);
            copy.setName(method.getName() + "Async");
            ctClass.addMethod(copy);

            if (isVoid(method)) {
                method.setBody("com.ezerium.utils.javassist.Util.runAsync(\"" + method.getName() + "\", this, $args);");
            } else {
                method.setBody("return ($r) com.ezerium.utils.javassist.Util.runAsyncWithReturn(\"" + method.getName() + "\", this, $args);");
            }

        } catch (Exception e) {
            throw new JavassistBuildException(e);
        }
    }

    @Override
    public TransformTarget getTarget() {
        return TransformTarget.METHOD;
    }

}
