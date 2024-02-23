package com.ezerium.spigot.command;

import com.ezerium.shared.annotations.Async;
import com.ezerium.shared.annotations.command.*;
import com.ezerium.shared.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    private CommandMap commandMap;
    private Map<String, org.bukkit.command.Command> knownCommands;

    private Map<String, CommandNode> registeredCommands;

    private void updateMap() {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        if (pluginManager != null && pluginManager instanceof SimplePluginManager) {
            SimplePluginManager simplePluginManager = (SimplePluginManager) pluginManager;
            try {
                this.commandMap = (CommandMap) ReflectionUtils.getFieldValue(simplePluginManager, "commandMap");

                if (this.commandMap != null && this.commandMap instanceof SimpleCommandMap) {
                    this.knownCommands = (Map<String, org.bukkit.command.Command>) ReflectionUtils.getFieldValue(this.commandMap, "knownCommands");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CommandHandler() {
        this.updateMap();

        this.registeredCommands = new HashMap<>();
    }

    public void register(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                this.register(method);
            }
        }
    }

    public void register(Method method) {
        Command annotation = method.getAnnotation(Command.class);
        if (annotation.value().split(" ").length > 1) {
            String command = annotation.value().split(" ")[0];

            CommandNode parent;
            if (!this.registeredCommands.containsKey(command)) {
                parent = new CommandNode(
                        null,
                        command,
                        new String[0],
                        null,
                        null,
                        method.getAnnotation(Usage.class),
                        null,
                        null,
                        null
                );

                this.registeredCommands.put(command, null);
            } else parent = this.registeredCommands.get(command);

            parent.addChild(new CommandNode(
                    method,
                    annotation.value().replace(command + " ", ""),
                    method.getAnnotation(Aliases.class) == null ? annotation.aliases() : method.getAnnotation(Aliases.class).value(),
                    method.getAnnotation(Description.class),
                    method.getAnnotation(Permission.class),
                    method.getAnnotation(Usage.class),
                    method.getAnnotation(Async.class),
                    method.getAnnotation(Cooldown.class),
                    parent
            ));

            return;
        }

        Aliases aliases = method.getAnnotation(Aliases.class);
        CommandNode node = new CommandNode(
                method,
                annotation.value(),
                aliases == null ? annotation.aliases() : aliases.value(),
                method.getAnnotation(Description.class),
                method.getAnnotation(Permission.class),
                method.getAnnotation(Usage.class),
                method.getAnnotation(Async.class),
                method.getAnnotation(Cooldown.class),
                null
        );
        EzCommand command = new EzCommand(node);
        EzHelpTopic helpTopic = new EzHelpTopic(node);

        EzCommand oldCommand = (EzCommand) this.knownCommands.get(command.getName());
        if (oldCommand != null) {
            oldCommand.unregister(this.commandMap);
        }

        this.commandMap.register(command.getName(), command);
        Bukkit.getServer().getHelpMap().addTopic(helpTopic);
    }

}
