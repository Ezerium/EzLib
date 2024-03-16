package com.ezerium.spigot.chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatInputListener implements Listener {

    @EventHandler
    public void onInput(AsyncPlayerChatEvent e) {
        if (ChatInput.INPUT_MAP.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            ChatInput input = ChatInput.INPUT_MAP.get(e.getPlayer());

            // remove before execution since errors may occur in the execution
            ChatInput.INPUT_MAP.remove(e.getPlayer());
            input.getExecution().accept(e.getMessage());
        }
    }

}
