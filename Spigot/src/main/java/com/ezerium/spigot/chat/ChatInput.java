package com.ezerium.spigot.chat;

import com.ezerium.spigot.Spigot;
import com.ezerium.spigot.utils.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@AllArgsConstructor
public class ChatInput {

    public static final Map<Player, ChatInput> INPUT_MAP = new HashMap<>();

    @Getter
    private final Consumer<String> execution;
    private boolean async = false;
    private long expireAfter = -1L;
    private String expireMessage = "&cTime expired.";

    public ChatInput(Consumer<String> execution) {
        this.execution = execution;
    }

    public ChatInput(Consumer<String> execution, long expireAfter) {
        this.execution = execution;
        this.expireAfter = expireAfter;
    }

    public ChatInput(Consumer<String> execution, long expireAfter, String expireMessage) {
        this.execution = execution;
        this.expireAfter = expireAfter;
        this.expireMessage = expireMessage;
    }

    public ChatInput(Consumer<String> execution, boolean async) {
        this.execution = execution;
        this.async = async;
    }

    public ChatInput(Consumer<String> execution, boolean async, long expireAfter) {
        this.execution = execution;
        this.async = async;
        this.expireAfter = expireAfter;
    }

    public void send(Player player) {
        INPUT_MAP.put(player, this);
        if (expireAfter != -1L) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(Spigot.INSTANCE.getPlugin(), () -> {
                if (INPUT_MAP.containsKey(player)) {
                    INPUT_MAP.remove(player);
                    player.sendMessage(Util.format(expireMessage));
                }
            }, expireAfter);
        }
    }

}
