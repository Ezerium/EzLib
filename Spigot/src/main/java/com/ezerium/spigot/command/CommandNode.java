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
import com.ezerium.utils.LoggerUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class CommandNode {

    private final CommandHandler handler;

    @Nullable
    private final CommandNode parentNode;

    private final String name;
    private final String[] aliases;
    private final String description;
    private final String permission;
    private final String usage;

    private final boolean async;
    private final boolean hidden;

    private final boolean cooldown;
    private final int cooldownTime;
    @Nullable
    private final String cooldownBypassPermission;

    @Nullable
    private final Method method;

    private final Map<String, CommandNode> children;

    public CommandNode(CommandHandler handler, @Nullable Method method, String name, String[] aliases, boolean hidden, @Nullable Description description, @Nullable Permission permission, @Nullable Usage usage, @Nullable Async async, @Nullable Cooldown cooldown, @Nullable CommandNode parent) {
        this.handler = handler;
        this.method = method;
        this.name = name;
        this.aliases = aliases;
        this.description = (description == null || description.value().isEmpty() ? "" : description.value());
        this.permission = (permission == null || permission.value().isEmpty() ? "" : permission.value());
        this.usage = (usage == null || usage.value().isEmpty() ? "" : usage.value());

        this.async = async != null;
        this.hidden = hidden;

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
        if (this.permission.isEmpty()) return true;
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
        if (!this.usage.isEmpty()) {
            String usage = this.usage;
            usage = usage.replaceAll("\\{command}", this.name);

            String regex = "\\{args:(\\d+)}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(usage);
            if (matcher.find()) {
                AtomicReference<String> finalUsage = new AtomicReference<>(usage);
                for (int i = 0; i < matcher.groupCount(); i++) {
                    int index = Integer.parseInt(matcher.group(i)) - 1;
                    String parameterUsage = this.getParameterUsage(this.method.getParameters()[index]);
                    finalUsage.set(finalUsage.get().replaceAll(regex, parameterUsage));
                }
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

        Config config = Spigot.INSTANCE.getConfig();
        if (!this.children.isEmpty()) {
            String usage = config.getInvalidUsageList();

            StringBuilder builder = new StringBuilder();
            int x = 0;
            for (CommandNode child : this.children.values()) {
                String name = child.getName().split(" ")[0];
                String subname = child.getName().replaceFirst(name + " ", "");

                builder.append(config.getPrimaryColor())
                        .append("/")
                        .append(name)
                        .append(" ")
                        .append(config.getSecondaryColor())
                        .append(subname)
                        .append(" &r");
                for (int i = 1; i < child.getMethod().getParameterCount(); i++) {
                    Parameter parameter = child.getMethod().getParameters()[i];
                    builder.append(this.parseArgAsText(parameter))
                            .append(" ");
                }
                if (x < this.children.size() - 1) builder.append("\n");
                x++;
            }

            return Util.format(String.format(usage, builder.toString()));
        }

        String usage = config.getInvalidUsage();

        StringBuilder builder = new StringBuilder();
        builder.append("/")
                .append(this.name)
                .append(" ");

        for (int i = 1; i < this.method.getParameterCount(); i++) {
            Parameter parameter = this.method.getParameters()[i];
            builder.append(this.parseArgAsText(parameter))
                    .append(" ");
        }

        return Util.format(String.format(usage, builder));
    }

    private String parseArgAsText(Parameter parameter) {
        if (parameter.isAnnotationPresent(Arg.class)) {
            Arg arg = parameter.getAnnotation(Arg.class);
            String value = arg.value();
            if (arg.defaultValue().isEmpty()) {
                return "<" + value + (arg.wildcard() ? "..." : "") + ">";
            }

            return "[" + value + (arg.wildcard() ? "..." : "") + "]";
        }

        if (parameter.isAnnotationPresent(Flag.class)) {
            Flag flag = parameter.getAnnotation(Flag.class);
            return "[-" + flag.value() + "]";
        }

        if (parameter.isAnnotationPresent(FlagValue.class)) {
            FlagValue flagValue = parameter.getAnnotation(FlagValue.class);
            return "[-" + flagValue.flagName() + " " + (flagValue.defaultValue().isEmpty() ? "<" : "[") + flagValue.argName() + (flagValue.defaultValue().isEmpty() ? ">" : "]") +  "]";
        }

        return "(ERROR)";
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
            String[] newArgs = Arrays.copyOfRange(args, child.getName().replaceFirst(this.name + " ", "").split(" ").length, args.length);

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

        List<Parameter> parameters = Lists.newArrayList(this.method.getParameters()).subList(1, this.method.getParameters().length);
        ArgumentParser parser = new ArgumentParser(sender, this.handler);

        Map<Integer, Argument> parsedArgs = parser.parse(args, parameters.toArray(new Parameter[0]));
        if (parsedArgs.size() > parameters.size() || parsedArgs.size() < parameters.size()) {
            sender.sendMessage(this.getUsageMessage());
            return;
        }

        if (parsedArgs.values().stream().anyMatch(arg -> {
            boolean a = arg.getValue() == null && arg.getType() != ArgumentType.FLAG_VALUE;
            if (a && arg.isA()) sender.sendMessage(this.getUsageMessage());
            return a;
        })) {
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
        if (args.length == 0) return null;

        for (int i = args.length; i > 0; i--) {
            String arg = String.join(" ", Arrays.copyOfRange(args, 0, i));
            if (arg.isEmpty()) return null;

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

        CommandNode child = this.foundChild(args);
        if (child != null) {
            String[] split = child.getName().split(" ");
            int length = split.length - 1;
            if (args.length == length) {
                String[] newArgs = Arrays.copyOfRange(args, child.getName().replaceFirst(this.name + " ", "").split(" ").length, args.length);
                return child.tabComplete(sender, newArgs);
            }
        }

        Method method = this.method;
        if (child != null) method = child.getMethod();
        if (method == null) return new ArrayList<>();

        List<String> completions = new ArrayList<>();

        int length = args.length;
        String lastArg = args[length - 1];

        mainLoop: for (CommandNode childNode : this.children.values()) {
            String[] name = childNode.getName().split(" ");
            name = Arrays.copyOfRange(name, 1, name.length);
            if (length != name.length) continue;

            for (int i = 0; i < length - 1; i++) {
                if (!name[i].equalsIgnoreCase(args[i])) {
                    continue mainLoop;
                }
            }

            String childName = name[length - 1];
            if (childName.toLowerCase().startsWith(lastArg.toLowerCase())) {
                completions.add(childName);
            }
        }

        List<Parameter> parameters = Lists.newArrayList(method.getParameters()).subList(1, method.getParameters().length);
        if (parameters.isEmpty()) return completions.stream().filter(completion -> completion.toLowerCase().startsWith(lastArg.toLowerCase())).collect(Collectors.toList());

        int index = length - 1;
        if (length >= 2) {
            int flagCount = (int) parameters.stream().filter(p -> p.isAnnotationPresent(FlagValue.class)).count();
            String previousArg = args[length - 2];
            int flagsDone = (int) Arrays.stream(args).filter(arg -> arg.startsWith("-")).count();
            if (flagsDone < flagCount && previousArg.startsWith("-")) {
                Parameter parameter = parameters.get(Arrays.asList(args).indexOf(previousArg));
                if (parameter.isAnnotationPresent(FlagValue.class)) {
                    FlagValue flagValue = parameter.getAnnotation(FlagValue.class);
                    if (flagValue.flagName().equals(previousArg.substring(1))) {
                        ParameterType<?> type = this.handler.getParameterTypes().get(parameter.getType());
                        if (type != null) {
                            return type.tabComplete(sender, lastArg);
                        }
                    }

                }
            }
        }

        if (index >= parameters.size()) return new ArrayList<>();

        Parameter parameter = parameters.get(length - 1);
        while (parameter.isAnnotationPresent(Flag.class) || parameter.isAnnotationPresent(FlagValue.class)) {
            index++;
            if (index >= parameters.size()) return new ArrayList<>();

            parameter = parameters.get(index);
        }

        ParameterType<?> type = this.handler.getParameterTypes().get(parameter.getType());
        if (type != null) completions.addAll(type.tabComplete(sender, lastArg));

        return completions.stream().filter(completion -> completion.toLowerCase().startsWith(lastArg.toLowerCase())).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "CommandNode{" +
                "name='" + name + '\'' +
                ", aliases=" + Arrays.toString(aliases) +
                ", description='" + description + '\'' +
                ", permission='" + permission + '\'' +
                ", usage='" + usage + '\'' +
                ", async=" + async +
                ", hidden=" + hidden +
                ", cooldown=" + cooldown +
                ", cooldownTime=" + cooldownTime +
                ", cooldownBypassPermission='" + cooldownBypassPermission + '\'' +
                ", method=" + method +
                ", children=" + children +
                '}';
    }

}
