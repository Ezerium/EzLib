package com.ezerium.spigot.command;

import com.ezerium.shared.logger.EzLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class EzCommand extends Command {

    private final CommandNode node;

    public EzCommand(CommandNode node) {
        super(node.getName(), node.getDescription(), node.getUsage(), List.of(node.getAliases()));
        this.node = node;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (node.isAsync()) {

        } else {
            try {

            } catch (Exception e) {
                EzLogger.logError("An error occurred while executing command " + node.getName() + " for " + commandSender.getName() + ": " + e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }
}
