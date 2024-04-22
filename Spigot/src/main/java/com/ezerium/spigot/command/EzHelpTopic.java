package com.ezerium.spigot.command;

import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

@AllArgsConstructor
public class EzHelpTopic extends HelpTopic {

    private final CommandNode node;

    @Override
    public boolean canSee(CommandSender commandSender) {
        return node.hasPermission(commandSender);
    }

    @Override
    public String getName() {
        return node.getName();
    }

    @Override
    public String getFullText(CommandSender forWho) {
        return node.getDescription();
    }

    @Override
    public String getShortText() {
        return node.getDescription().length() > 30 ? node.getDescription().substring(0, 3) + "..." : node.getDescription();
    }

}
