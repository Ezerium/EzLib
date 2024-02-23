package com.ezerium.spigot.command;

import com.ezerium.shared.annotations.Async;
import com.ezerium.shared.annotations.command.*;
import com.ezerium.spigot.Config;
import com.ezerium.spigot.Spigot;
import com.ezerium.spigot.utils.Util;
import lombok.Data;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Data
public class CommandNode {

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

    public CommandNode(@Nullable Method method, String name, String[] aliases, @Nullable Description description, @Nullable Permission permission, @Nullable Usage usage, @Nullable Async async, @Nullable Cooldown cooldown, @Nullable CommandNode parent) {
        this.method = method;
        this.name = name;
        this.aliases = aliases;
        this.description = (description.value().isEmpty() ? null : description.value());
        this.permission = (permission.value().isEmpty() ? null : permission.value());
        this.usage = (usage.value().isEmpty() ? null : usage.value());

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

    public String getUsageMessage() {
        if (this.usage != null) {
            String usage = this.usage;
            usage = usage.replaceAll("\\{command}", this.name);
            //usage = usage.replaceAll("\\{arg\\:([0-9]+)}", "<$1>");

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

}
