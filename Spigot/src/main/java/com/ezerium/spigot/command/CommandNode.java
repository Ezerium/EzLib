package com.ezerium.spigot.command;

import com.ezerium.annotations.Async;
import com.ezerium.annotations.command.*;
import com.ezerium.logger.EzLogger;
import com.ezerium.utils.CooldownUtil;
import com.ezerium.spigot.Config;
import com.ezerium.spigot.Spigot;
import com.ezerium.spigot.command.arguments.Argument;
import com.ezerium.spigot.command.arguments.ArgumentParser;
import com.ezerium.spigot.command.arguments.ArgumentType;
import com.ezerium.spigot.command.parameters.ParameterType;
import com.ezerium.spigot.utils.Util;
import lombok.Data;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class CommandNode {

    private final CommandHandler handler;

    @Nullable
    private final CommandNode parentNode;

    private final String name;
    private final String[] aliases;
    @Nullable
    private final String description;
    @Nullable
    private final String permission;
    @Nullable
    private final String usage;

    private final boolean async;

    private final boolean cooldown;
    private final int cooldownTime;
    @Nullable
    private final String cooldownBypassPermission;

    @Nullable
    private final Method method;

    private final Map<String, CommandNode> children;

    public CommandNode(CommandHandler handler, @Nullable Method method, String name, String[] aliases, @Nullable Description description, @Nullable Permission permission, @Nullable Usage usage, @Nullable Async async, @Nullable Cooldown cooldown, @Nullable CommandNode parent) {
        this.handler = handler;
        this.method = method;
        this.name = name;
        this.aliases = aliases;
        this.description = (description == null || description.value().isEmpty() ? null : description.value());
        this.permission = (permission == null || permission.value().isEmpty() ? null : permission.value());
        this.usage = (usage == null || usage.value().isEmpty() ? null : usage.value());

        this.async = async != null;

        this.cooldown = cooldown != null;
        this.cooldownTime = (this.cooldown ? cooldown.value() : 0);
        this.cooldownBypassPermission = (this.cooldown ? cooldown.bypassPermission() : null);

        this.children = new HashMap<>();
        this.parentNode = parent;
    }

    public void addChild(CommandNode child) {
        String childName = child.getName().replaceFirst(this.name + " ", "");
        this.children.put(childName, child);

        for (String alias : child.getAliases()) {
            this.children.put(alias.replaceFirst(this.name + " ", ""), child);
        }
    }

    public boolean hasPermission(CommandSender sender) {
        if (this.permission == null) return true;
        if (!(sender instanceof Player)) return true;

        if (!sender.isOp() && this.permission.equals("op")) return false;

        if (sender.isOp()) return true;
        if (sender.hasPermission("*") || sender.hasPermission("*.*")) return true;

        return sender.hasPermission(this.permission);
    }

    private String getParameterUsage(Parameter parameter) {
        if (parameter.isAnnotationPresent(Arg.class)) {
            Arg arg = parameter.getAnnotation(Arg.class);
            return (arg.defaultValue().isEmpty() ? "<" : "[") + arg.value() + (arg.wildcard() ? "..." : "") + (arg.defaultValue().isEmpty() ? ">" : "]");
        }

        if (parameter.isAnnotationPresent(Flag.class)) {
            Flag flag = parameter.getAnnotation(Flag.class);
            return "[-" + flag.value() + "]";
        }

        if (parameter.isAnnotationPresent(FlagValue.class)) {
            FlagValue flagValue = parameter.getAnnotation(FlagValue.class);
            return "[-" + flagValue.flagName() + " <" + flagValue.argName() + ">]";
        }

        return "";
    }

    public String getUsageMessage() {
        if (this.usage != null) {
            String usage = this.usage;
            usage = usage.replaceAll("\\{command}", this.name);

            String regex = "\\{args:(\\d+)}";
            Pattern pattern = Pattern.compile(regex);
            if (pattern.matcher(usage).find()) {
                AtomicReference<String> finalUsage = new AtomicReference<>(usage);
                pattern.matcher(usage).results().forEach(matchResult -> {
                    int index = Integer.parseInt(matchResult.group(1)) - 1;
                    String parameterUsage = this.getParameterUsage(this.method.getParameters()[index]);
                    finalUsage.set(finalUsage.get().replaceAll(regex, parameterUsage));
                });
            } else {
                Method m = this.method;
                if (m == null) m = this.parentNode.getMethod();
                if (m == null) throw new RuntimeException("what?? u somehow managed to get here without a method, nice job!");

                String parameterUsage = "";
                for (int i = 1; i < this.method.getParameters().length; i++) {
                    parameterUsage += this.getParameterUsage(this.method.getParameters()[i]) + " ";
                }

                usage = usage.replaceAll("\\{args}", parameterUsage.trim());
            }

            return Util.format(usage);
        }

        // TODO: generate usage message whereas if the command has children, it will list them and if only one or no children, it will display a simple usage of the command
        Config config = Spigot.INSTANCE.getConfig();
        if (!this.children.isEmpty()) {
            String usage = config.getInvalidUsageList();

            StringBuilder builder = new StringBuilder();
            int i = 0;
            for (CommandNode child : this.children.values()) {
                String name = child.getName().split(" ")[0];
                String subname = child.getName().replaceFirst(name + " ", "");

                builder.append(config.getPrimaryColor())
                        .append("/")
                        .append(name)
                        .append(" ")
                        .append(config.getSecondaryColor())
                        .append(subname)
                        .append(" &r")
                        .append("todo: show args")
                        .append("\n");
                i++;
            }

            return Util.format(String.format(usage, builder.toString()));
        }

        String usage = config.getInvalidUsage();

        StringBuilder builder = new StringBuilder();
        builder.append("/")
                .append(this.name)
                .append(" ");
        // todo: finish

        return Util.format(String.format(usage, builder.toString()));
    }

    public void execute(CommandSender sender, String[] args) {
        if (!this.hasPermission(sender)) {
            sender.sendMessage(Util.format(Spigot.INSTANCE.getConfig().getNoPermission()));
            return;
        }

        if (this.method == null) {
            sender.sendMessage(this.getUsageMessage());
            return;
        }

        Parameter executorParameter = this.method.getParameters()[0];
        if (executorParameter.getType() == Player.class && !(sender instanceof Player)) {
            sender.sendMessage(Util.format(Spigot.INSTANCE.getConfig().getPlayerOnly()));
            return;
        }

        if (executorParameter.getType() == ConsoleCommandSender.class && sender instanceof Player) {
            sender.sendMessage(Util.format(Spigot.INSTANCE.getConfig().getConsoleOnly()));
            return;
        }

        CommandNode child = this.foundChild(args);
        if (child != null) {
            String subCommand = String.join(" ", args);
            String[] newArgs = subCommand.replaceFirst(child.getName() + " ", "").split(" ");

            child.execute(sender, newArgs);
            return;
        }

        List<Object> arguments = new ArrayList<>();
        arguments.add(sender);

        if (this.cooldown) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission(this.cooldownBypassPermission)) {
                    if (CooldownUtil.isOnCooldown(player.getUniqueId().toString(), this.cooldownTime)) {
                        sender.sendMessage(Util.format(Spigot.INSTANCE.getConfig().getOnCooldown()));
                        return;
                    }

                    CooldownUtil.setCooldown(player.getUniqueId().toString());
                }
            }
        }

        List<Parameter> parameters = List.of(this.method.getParameters()).subList(1, this.method.getParameters().length);
        ArgumentParser parser = new ArgumentParser(sender, this.handler);

        Map<Integer, Argument> parsedArgs = parser.parse(args, parameters.toArray(new Parameter[0]));
        if (parsedArgs.size() > parameters.size() || parsedArgs.size() < parameters.size()) {
            sender.sendMessage(this.getUsageMessage());
            return;
        }

        if (parsedArgs.values().stream().anyMatch(arg -> arg.getValue() == null && arg.getType() != ArgumentType.FLAG_VALUE)) {
            sender.sendMessage(this.getUsageMessage());
            return;
        }

        for (int i = 0; i < parameters.size(); i++) {
            Argument arg = parsedArgs.get(i);
            arguments.add(arg.getValue());
        }

        try {
            Class<?> declaringClass = this.method.getDeclaringClass();
            this.method.invoke(declaringClass.getDeclaredConstructor().newInstance(), arguments.toArray());
        } catch (Exception e) {
            EzLogger.logError("An error occurred while executing command " + this.name + " for " + sender.getName() + ": " + e.getMessage(), e);
            sender.sendMessage(Util.format("&cAn error occurred whilst attempting to execute this command."));
            if (e.getCause() != null) {
                sender.sendMessage(Util.format("&cCause: " + e.getCause().getMessage()));
            }
        }
    }

    private CommandNode foundChild(String[] args) {
        for (int i = args.length; i > 0; i--) {
            String arg = String.join(" ", args);
            if (this.children.containsKey(arg)) {
                return this.children.get(arg);
            }
        }

        return null;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (!this.hasPermission(sender)) return new ArrayList<>();

        if (this.method == null) {
            return new ArrayList<>();
        }

        String arg = args[args.length - 1];
        List<String> completions = new ArrayList<>();

        for (CommandNode child : this.children.values()) {
            String[] split = child.getName().split(" ");
            int index = args.length - 1;
            if (index + 1 > split.length) continue;

            String subCommand = split[index];
            completions.add(subCommand);
        }

        int index = args.length - 1;
        if (this.method.getParameterCount() - 1 < index) return completions.stream().filter(s -> s.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());

        if (index - 1 >= 0) {
            String prevArg = args[index - 1];
            if (prevArg.startsWith("-")) {
                Parameter flagValueParam = null;
                for (int i = index; i < this.method.getParameters().length; i++) {
                    Parameter param = this.method.getParameters()[i];
                    if (param.isAnnotationPresent(FlagValue.class)) {
                        FlagValue flagValue = param.getAnnotation(FlagValue.class);
                        if (arg.equals("-" + flagValue.flagName())) {
                            flagValueParam = param;
                            break;
                        }
                    }
                }

                if (flagValueParam != null) {
                    ParameterType<?> parameterType = this.handler.getParameterTypes().get(flagValueParam.getType());
                    if (parameterType != null) {
                        completions.addAll(parameterType.tabComplete(sender, arg));
                        return completions.stream().filter(s -> s.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
                    }
                }
            }
        }

        Parameter parameter = this.method.getParameters()[index];
        while (parameter.isAnnotationPresent(Flag.class) || parameter.isAnnotationPresent(FlagValue.class)) {
            index++;
            if (this.method.getParameterCount() - 1 < index) return completions.stream().filter(s -> s.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
            parameter = this.method.getParameters()[index];
        }

        ParameterType<?> parameterType = this.handler.getParameterTypes().get(parameter.getType());
        if (parameterType != null) {
            completions.addAll(parameterType.tabComplete(sender, arg));
        }

        return completions.stream().filter(s -> s.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
    }

}
