package com.ezerium.shared.bytecode;

import com.ezerium.shared.annotations.Debug;
import com.ezerium.shared.logger.debug.DebugAt;
import com.ezerium.shared.utils.javassist.Util;
import de.icongmbh.oss.maven.plugin.javassist.ClassTransformer;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.build.JavassistBuildException;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

public class DebugTransformer extends ClassTransformer {

    @Override
    public void applyTransformations(CtClass ctClass) throws JavassistBuildException {
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            if (method.hasAnnotation(Debug.class)) {
                try {
                    Debug debug = (Debug) method.getAnnotation(Debug.class);

                    String logOnCall = debug.logOnCall();
                    DebugAt debugAt = debug.debugAt();

                    Pattern pattern = Pattern.compile("\\{[\\w._]+}");
                    if (pattern.matcher(logOnCall).find()) {
                        String[] split = logOnCall.split("\\{");
                        StringBuilder sb = new StringBuilder();
                        for (String s : split) {
                            if (s.contains("}")) {
                                String[] split1 = s.split("}");
                                sb.append("\" + ").append(split1[0]).append(" + \"");
                                if (split1.length > 1) {
                                    sb.append(split1[1]);
                                }
                            } else {
                                sb.append(s);
                            }
                        }
                        logOnCall = sb.toString();
                    }

                    if (debugAt == DebugAt.START) {
                        method.insertAfter("com.ezerium.shared.logger.EzLogger.debug(\"" + logOnCall + "\");");
                    } else if (debugAt == DebugAt.END) {
                        method.insertAfter("com.ezerium.shared.logger.EzLogger.debug(\"" + logOnCall + "\");");
                    }

                    Util.deleteAnnotationIfPresent(method, Debug.class);
                } catch (Exception e) {
                    throw new JavassistBuildException(e);
                }
            }
        }
    }

    @Override
    public boolean shouldTransform(CtClass ctClass) throws JavassistBuildException {
        return Arrays.stream(ctClass.getDeclaredMethods()).anyMatch(m -> m.hasAnnotation(Debug.class));
    }

    @Override
    public void configure(Properties properties) throws Exception {
        if (properties == null) {
            return;
        }
    }
}
