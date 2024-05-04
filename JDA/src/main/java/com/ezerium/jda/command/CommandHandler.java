package com.ezerium.jda.command;

import com.ezerium.annotations.command.Command;
import com.ezerium.jda.EzBot;
import com.ezerium.utils.LoggerUtil;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler {

    @Getter
    private static final Map<String, CommandNode> registeredCommands = new HashMap<>();
    @Getter
    @Setter
    private static String prefix = "!";

    @Deprecated
    public enum Type {
        ANNOTATION,
        INTERFACE
    }

    @Deprecated
    private final Type type;

    public CommandHandler() {
        this(Type.INTERFACE);
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

        String name = command.getName();
        String description = command.getDescription();

        CommandNode node = new CommandNode(name, description, command);
        registeredCommands.put(name, node);
    }

}
