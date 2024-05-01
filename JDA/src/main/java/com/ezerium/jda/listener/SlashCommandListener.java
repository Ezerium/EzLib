package com.ezerium.jda.listener;

import com.ezerium.jda.annotations.Listener;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SlashCommandListener implements EzListener {

    @Listener(SlashCommandInteractionEvent.class)
    public void onSlashCommand(SlashCommandInteractionEvent e) {
        SlashCommandInteraction commandData = e.getInteraction();
    }

    @Listener(MessageReceivedEvent.class)
    public void onMessageReceived(MessageReceivedEvent e) {
        String message = e.getMessage().getContentRaw();
    }
}
