package com.ezerium.jda.command;

import com.ezerium.annotations.command.Command;
import com.ezerium.utils.LoggerUtil;
import com.google.common.base.Preconditions;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

public class CommandHandler {

    @Deprecated
    public enum Type {
        ANNOTATION,
        INTERFACE
    }

    @Deprecated
    private final Type type;

    public CommandHandler() {
        this(Type.ANNOTATION);
    }

    @Deprecated
    public CommandHandler(Type type) {
        this.type = type;
    }

    public void register(Class<?> clazz) {
        if (clazz.isAssignableFrom(ICommand.class)) {
            try {
                Object o = clazz.getDeclaredConstructor().newInstance();
                register((ICommand) o);
            } catch (Exception e) {
                LoggerUtil.warn("Tried to register a ICommand class '" + clazz.getSimpleName() +  "' in " + clazz.getPackage().getName() + ", but failed.");
                LoggerUtil.warn("If you require arguments in your constructor, please use the register(ICommand) method instead or ignore if you are using CommandHandler#register(ICommand).");
            }
        } else {
            for (Method method : clazz.getDeclaredMethods()) {
                register(method);
            }
        }
    }

    @Deprecated
    public void register(Method method) {
        Preconditions.checkState(type == Type.ANNOTATION, "Cannot register Method with Type.INTERFACE");
        if (!method.isAnnotationPresent(Command.class)) return;


    }

    public void register(ICommand command) {
        Preconditions.checkState(type == Type.INTERFACE, "Cannot register ICommand with Type.ANNOTATION");


    }

}
