package com.ezerium.jda.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.ezerium.jda.annotations.Bot")
public class BotProcessor extends AbstractProcessor {

    private ProcessingEnvironment processingEnv;
    private Messager messager;
    private Filer filer;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.processingEnv = processingEnv;
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
        this.elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (!element.getKind().isClass() || !(element instanceof TypeElement)) continue;

                TypeElement typeElement = (TypeElement) element;
                String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
                String className = typeElement.getSimpleName().toString();

                if (roundEnv.getElementsAnnotatedWith(annotation).size() > 1) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Only one class can be annotated with @Bot.");
                    return false;
                }

                if (!processingEnv.getTypeUtils().isAssignable(typeElement.asType(), elementUtils.getTypeElement("com.ezerium.jda.EzBot").asType())) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "The class annotated with @Bot must extend EzBot.");
                    return false;
                }

                try {
                    filer.getResource(StandardLocation.SOURCE_PATH, "", packageName + ".Main");
                    throw new RuntimeException("Main class already exists in package " + packageName);
                } catch (IOException ignored) {
                    // doesnt exist
                }

                generateMainClass(packageName, className);
                generateManifest(packageName);
            }
        }

        return false;
    }

    private void generateMainClass(String packageName, String className) {
        try {
            FileObject fileObject = filer.createSourceFile(packageName + ".Main");
            try (BufferedWriter writer = new BufferedWriter(fileObject.openWriter())) {
                writer.write(getMainClassContent(packageName, className));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateManifest(String packageName) {
        try {
            FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/MANIFEST.MF");
            try (BufferedWriter writer = new BufferedWriter(fileObject.openWriter())) {
                writer.write(getManifestContent(packageName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMainClassContent(String packageName, String className) {
        return "package " + packageName + ";\n" +
                "\n" +
                "public final class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        new " + className + "().start(args);\n" +
                "    }\n" +
                "}";
    }

    private String getManifestContent(String packageName) {
        return "Manifest-Version: 1.0\n" +
                "Main-Class: " + packageName + ".Main\n";
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
