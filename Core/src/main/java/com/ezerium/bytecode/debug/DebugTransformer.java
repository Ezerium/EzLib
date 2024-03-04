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
import java.util.ArrayList;
import java.util.List;
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
            Matcher matcher = pattern.matcher(logOnCall);
            boolean found = matcher.find();
            List<String> variables = new ArrayList<>();
            if (found) {
                String[] split = logOnCall.split("\\{");
                StringBuilder sb = new StringBuilder();
                for (String s : split) {
                    if (s.contains("}")) {
                        String[] split1 = s.split("}");
                        variables.add(split1[0]);

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
                method.insertAfter("com.ezerium.logger.EzLogger.debug(\"" + logOnCall + "\");");
            } else if (debugAt == DebugAt.END) {
                method.insertAfter("com.ezerium.logger.EzLogger.debug(\"" + logOnCall + "\");");
            } else {
                // find the last assignment or variable from {variableName} and insert after that
                if (!variables.isEmpty()) {
                    String variableName = variables.get(0);

                    int index = Util.findLastAssignmentOrVariable(method, variableName);
                    if (index == -1) {
                        index = 0;
                    }

                    method.insertAt(index, "com.ezerium.logger.EzLogger.debug(\"" + logOnCall + "\");");
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
