package com.ezerium.spigot.command;

import com.ezerium.logger.EzLogger;
import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EzCommand extends Command {

    private final CommandNode node;

    public EzCommand(CommandNode node) {
        super(node.getName(), node.getDescription(), node.getUsage(), Lists.newArrayList(node.getAliases()));
        this.node = node;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (node.isAsync()) {
            CompletableFuture.runAsync(() -> {
                try {
                    node.execute(sender, args);
                } catch (Exception e) {
                    EzLogger.logError("An error occurred while executing command " + node.getName() + " for " + sender.getName() + ": " + e.getMessage(), e);
                    sender.sendMessage("An error occurred while executing this command.");
                    if (e.getCause() != null) {
                        sender.sendMessage("Cause: " + e.getCause().getMessage());
                    }
                }
            });
        } else {
            try {
                node.execute(sender, args);
            } catch (Exception e) {
                EzLogger.logError("An error occurred while executing command " + node.getName() + " for " + sender.getName() + ": " + e.getMessage(), e);
                sender.sendMessage("An error occurred while executing this command.");
                if (e.getCause() != null) {
                    sender.sendMessage("Cause: " + e.getCause().getMessage());
                }

                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = super.tabComplete(sender, alias, args);
        completions.addAll(
                node.tabComplete(sender, args).stream().filter(s -> !completions.contains(s)).collect(Collectors.toList())
        );

        return completions;
    }
}
