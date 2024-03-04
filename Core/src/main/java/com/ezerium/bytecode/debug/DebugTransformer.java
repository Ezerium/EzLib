package com.ezerium.bytecode.debug;

import com.ezerium.annotations.TransformByAnnotation;
import com.ezerium.logger.debug.DebugAt;
import com.ezerium.annotations.Debug;
import com.ezerium.bytecode.EzClassTransformer;
import com.ezerium.utils.javassist.Util;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.build.JavassistBuildException;

import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@TransformByAnnotation(Debug.class)
public class DebugTransformer extends EzClassTransformer {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return Debug.class;
    }

    @Override
    public void transformMethod(CtMethod method) throws JavassistBuildException {
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

            System.out.println(method.getLongName());
            if (debugAt == DebugAt.START) {
                method.insertAfter("com.ezerium.logger.EzLogger.debug(\"" + logOnCall + "\");");
            } else if (debugAt == DebugAt.END) {
                method.insertAfter("com.ezerium.logger.EzLogger.debug(\"" + logOnCall + "\");");
            } else {
                // find the last assignment or variable from {variableName} and insert after that
                Matcher matcher = pattern.matcher(logOnCall);
                if (matcher.find()) {
                    String group = matcher.group();
                    String variableName = group.substring(1, group.length() - 1);

                    method.insertAt(Util.findLastAssignmentOrVariable(method, variableName), "com.ezerium.logger.EzLogger.debug(\"" + logOnCall + "\");");
                } else {
                    method.insertAfter("com.ezerium.logger.EzLogger.debug(\"" + logOnCall + "\");");
                }
            }

            Util.deleteAnnotationIfPresent(method, Debug.class);
        } catch (Exception e) {
            throw new JavassistBuildException(e);
        }
    }

    @Override
    public TransformTarget getTarget() {
        return TransformTarget.METHOD;
    }

    @Override
    public void configure(Properties properties) throws Exception {
        if (properties == null) {
            return;
        }
    }
}
