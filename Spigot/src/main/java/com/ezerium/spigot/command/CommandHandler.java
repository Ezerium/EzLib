package com.ezerium.spigot.command;

import com.ezerium.annotations.Async;
import com.ezerium.annotations.command.*;
import com.ezerium.utils.ClassUtil;
import com.ezerium.utils.ReflectionUtils;
import com.ezerium.spigot.Spigot;
import com.ezerium.spigot.command.parameters.ParameterType;
import com.ezerium.spigot.command.parameters.impl.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler {

    private CommandMap commandMap;
    private Map<String, org.bukkit.command.Command> knownCommands;

    private Map<String, CommandNode> registeredCommands;

    @Getter
    private Map<Class<?>, ParameterType<?>> parameterTypes;

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
        this.parameterTypes = new HashMap<>();

        this.init();
    }

    public void registerParameterType(Class<?> clazz, ParameterType<?> parameterType) {
        this.parameterTypes.put(clazz, parameterType);
    }

    public void registerAll(JavaPlugin plugin) {
        for (Class<?> clazz : ClassUtil.getClassesInPackage(plugin.getClass(), plugin.getClass().getPackage().getName())) {
            this.register(clazz);
        }
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
        Aliases aliases = method.getAnnotation(Aliases.class);

        String name = annotation.value();
        if (!name.contains(" ")) {
            CommandNode node = new CommandNode(
                    this,
                    method,
                    annotation.value(),
                    aliases == null ? annotation.aliases() : aliases.value(),
                    annotation.hidden(),
                    method.getAnnotation(Description.class),
                    method.getAnnotation(Permission.class),
                    method.getAnnotation(Usage.class),
                    method.getAnnotation(Async.class),
                    method.getAnnotation(Cooldown.class),
                    null
            );

            this.registeredCommands.put(node.getName(), node);

            EzHelpTopic helpTopic = new EzHelpTopic(node);
            EzCommand command = new EzCommand(node);
            this.commandMap.register(Spigot.INSTANCE.getPlugin().getDescription().getName().toLowerCase().replace(" ", "_"), command);
            Bukkit.getServer().getHelpMap().addTopic(helpTopic);
        }

        for (Method child : method.getDeclaringClass().getDeclaredMethods()) {
            if (child.isAnnotationPresent(Command.class)) {
                Command childAnnotation = child.getAnnotation(Command.class);
                String childName = childAnnotation.value();
                if (!childName.contains(" ")) continue;
                if (!childName.startsWith(name + " ")) continue;

                CommandNode parent = this.registeredCommands.getOrDefault(name, null);
                if (parent == null) continue;

                Aliases childAliases = child.getAnnotation(Aliases.class);
                CommandNode node = new CommandNode(
                        this,
                        child,
                        childAnnotation.value(),
                        childAliases == null ? childAnnotation.aliases() : childAliases.value(),
                        childAnnotation.hidden(),
                        child.getAnnotation(Description.class),
                        child.getAnnotation(Permission.class),
                        child.getAnnotation(Usage.class),
                        child.getAnnotation(Async.class),
                        child.getAnnotation(Cooldown.class),
                        parent
                );

                parent.addChild(node);
                this.registeredCommands.put(node.getName(), node);
            }
        }
    }

    private void init() {
        this.registerParameterType(String.class, new StringParameterType());
        this.registerParameterType(Player.class, new PlayerParameterType());
        this.registerParameterType(int.class, new IntegerParameterType());
        this.registerParameterType(Integer.class, new IntegerParameterType());
        this.registerParameterType(boolean.class, new BooleanParameterType());
        this.registerParameterType(Boolean.class, new BooleanParameterType());
    }

}
