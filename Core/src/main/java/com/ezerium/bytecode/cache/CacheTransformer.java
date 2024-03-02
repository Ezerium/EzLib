package com.ezerium.bytecode.cache;

import com.ezerium.annotations.Cache;
import com.ezerium.annotations.TransformByAnnotation;
import com.ezerium.bytecode.EzClassTransformer;
import com.ezerium.utils.javassist.Util;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.build.JavassistBuildException;

import java.lang.annotation.Annotation;

//@TransformByAnnotation(Cache.class)
public class CacheTransformer extends EzClassTransformer {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Cache.class;
    }

    @Override
    public void transformMethod(CtMethod method) throws JavassistBuildException {
        ClassPool pool = ClassPool.getDefault();
        try {
            Cache cache = (Cache) method.getAnnotation(Cache.class);

            String id = method.getName();
            String cacheByParam = cache.cacheByParameter();

            String cacheId = "\"" + id + "\"" + (cacheByParam.isEmpty() ? "" : " + " + cacheByParam + ".toString()");
            method.insertBefore("if (com.ezerium.utils.CacheUtils.isCached(" + cacheId + ")) { return ($r) com.ezerium.utils.CacheUtils.getCache(" + cacheId + "); }");

            method.insertAfter("com.ezerium.utils.CacheUtils.put(" + cacheId + ", $_);");

            Util.deleteAnnotationIfPresent(method, Cache.class);
        } catch (Exception e) {
            throw new JavassistBuildException(e);
        }
    }

    @Override
    public TransformTarget getTarget() {
        return TransformTarget.METHOD;
    }
}
