package com.ezerium.bytecode.timer;

import com.ezerium.annotations.Timer;
import com.ezerium.bytecode.EzClassTransformer;
import com.ezerium.utils.StringUtil;
import com.ezerium.utils.javassist.Util;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.build.JavassistBuildException;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.UUID;

public class TimerTransformer extends EzClassTransformer {

    @Override
    public TransformTarget getTarget() {
        return TransformTarget.METHOD;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Timer.class;
    }

    @Override
    public void transformMethod(CtMethod method) throws JavassistBuildException {
        try {
            String id = UUID.randomUUID().toString().split("-")[0];
            method.insertBefore("com.ezerium.utils.timer.Timer.timers.put(\"" + id + "\", new com.ezerium.utils.timer.Timer().start());");
            method.insertAfter("System.out.println(\"Method " + method.getName() + " took \" + ((com.ezerium.utils.timer.Timer) com.ezerium.utils.timer.Timer.timers.get(\"" + id + "\")).stop().getMillisAndSeconds() + \" to execute.\");");

            method.getMethodInfo().rebuildStackMap(ClassPool.getDefault());
            Util.deleteAnnotationIfPresent(method, Timer.class);
        } catch (Exception e) {
            throw new JavassistBuildException(e);
        }
    }
}
