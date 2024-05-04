package com.ezerium.jda.listener;

import com.ezerium.jda.EzBot;
import com.ezerium.jda.annotations.Listener;
import com.ezerium.jda.command.CommandContext;
import com.ezerium.jda.command.CommandHandler;
import com.ezerium.jda.command.CommandNode;
import com.ezerium.jda.command.ICommand;
import com.ezerium.utils.LoggerUtil;
import com.ezerium.utils.ReflectionUtils;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandListener implements EzListener {

    @Listener(SlashCommandInteractionEvent.class)
    public void onSlashCommand(SlashCommandInteractionEvent e) {
        SlashCommandInteraction commandData = e.getInteraction();
        CommandNode node = CommandHandler.getRegisteredCommands().get(commandData.getName());
        if (node == null) return;

        node.execute(e);
    }

    @Listener(MessageReceivedEvent.class)
    public void onMessageReceived(MessageReceivedEvent e) {
        String message = e.getMessage().getContentRaw();
        String prefix = CommandHandler.getPrefix();
        if (!message.startsWith(prefix)) return;

        String[] args = message.substring(prefix.length()).split(" ");
        CommandNode node = CommandHandler.getRegisteredCommands().get(args[0]);
        if (node == null) return;
        if (node.getCommand().getType() != ICommand.CommandType.MESSAGE_COMMAND) return;

        node.execute(e.getMember(), args, e);
    }

    @Listener(GuildReadyEvent.class)
    public void onGuildReady(GuildReadyEvent e) {
        List<CommandData> commands = new ArrayList<>();
        for (CommandNode node : CommandHandler.getRegisteredCommands().values()) {
            if (node.getCommand().getType() != ICommand.CommandType.SLASH_COMMAND) continue;

            String name = node.getName();
            String description = node.getDescription();
            SlashCommandData data = Commands.slash(name, description);

            for (ICommand.Option option : node.getCommand().getOptions()) {
                data.addOption(option.getType(), option.getName(), option.getDescription(), option.isRequired());
            }

            commands.add(data);
        }

        e.getGuild().updateCommands().addCommands(commands).queue();
    }
}
