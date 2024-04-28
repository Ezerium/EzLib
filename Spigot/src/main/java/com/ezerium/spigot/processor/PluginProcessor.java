package com.ezerium.spigot.processor;

import com.ezerium.spigot.annotations.Plugin;
import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.ezerium.spigot.annotations.Plugin")
@AutoService(Processor.class)
public class PluginProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;
    private Elements elementUtils;
    private ProcessingEnvironment processingEnv;

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
        if (annotations.isEmpty()) return false;
        if (annotations.size() > 1) {
            this.messager.printMessage(Diagnostic.Kind.ERROR, "You may only register one @Plugin annotation per plugin.");
            return false;
        }

        TypeElement annotation = annotations.iterator().next();
        for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
            if (!element.getKind().isClass()) continue;
            if (!(element instanceof TypeElement)) continue;

            TypeElement typeElement = (TypeElement) element;
            if (!processingEnv.getTypeUtils().isAssignable(typeElement.asType(), elementUtils.getTypeElement("org.bukkit.plugin.java.JavaPlugin").asType())) {
                this.messager.printMessage(Diagnostic.Kind.ERROR, "The class annotated with @Plugin must extend JavaPlugin.");
                return false;
            }

            this.messager.printMessage(Diagnostic.Kind.NOTE, "Found @Plugin annotation in " + typeElement.getSimpleName());
            Plugin plugin = element.getAnnotation(Plugin.class);

            if (plugin.name().isEmpty() || plugin.version().isEmpty()) {
                this.messager.printMessage(Diagnostic.Kind.ERROR, "Missing important information in @Plugin annotation. Is 'name' and 'version' set?");
                return false;
            }

            this.messager.printMessage(Diagnostic.Kind.NOTE, "Generating plugin.yml for " + plugin.name());
            StringBuilder information = this.generateInformation(plugin, typeElement.getQualifiedName().toString());
            this.messager.printMessage(Diagnostic.Kind.NOTE, "The plugin.yml information has been created for " + plugin.name());

            this.writePluginYml(information, typeElement);
            this.messager.printMessage(Diagnostic.Kind.NOTE, "Finished! The plugin.yml file has been written for " + plugin.name());

            return true;
        }

        return false;
    }

    private void writePluginYml(StringBuilder information, TypeElement element) {
        try {
            FileObject fileObject = this.filer.createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml", element);
            File file = new File(fileObject.toUri());
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            writer.write(information.toString());
            writer.close();
        } catch (Exception e) {
            this.messager.printMessage(Diagnostic.Kind.ERROR, "Failed to write plugin.yml file.");
            e.printStackTrace();
        }
    }

    private StringBuilder generateInformation(Plugin plugin, String mainClassName) {
        List<String> depend = Lists.newArrayList(plugin.depend());
        depend.add("ProtocolLib");

        StringBuilder builder = new StringBuilder()
                .append("name: ").append(plugin.name()).append("\n")
                .append("version: ").append(plugin.version()).append("\n")
                .append("main: ").append(mainClassName).append("\n");
        if (!plugin.apiVersion().isEmpty()) builder.append("api-version: ").append(plugin.apiVersion()).append("\n");
        if (!plugin.description().isEmpty()) builder.append("description: ").append(plugin.description()).append("\n");
        if (plugin.authors().length > 0) {
            if (plugin.authors().length == 1) builder.append("author: ").append(plugin.authors()[0]).append("\n");
            else builder.append("authors: [").append(String.join(", ", plugin.authors())).append("]\n");
        }
        if (plugin.depend().length > 0) builder.append("depend: [").append(String.join(", ", depend)).append("]\n");
        if (plugin.softDepend().length > 0) builder.append("soft-depend: [").append(String.join(", ", plugin.softDepend())).append("]\n");
        if (plugin.loadBefore().length > 0) builder.append("load-before: [").append(String.join(", ", plugin.loadBefore())).append("]\n");
        if (plugin.loadAfter().length > 0) builder.append("load-after: [").append(String.join(", ", plugin.loadAfter())).append("]\n");
        return builder;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
