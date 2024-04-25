package com.ezerium.spigot.command.parameters.impl;

import com.ezerium.spigot.command.parameters.ParameterType;
import com.ezerium.spigot.utils.Util;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OfflinePlayerParameterType implements ParameterType<OfflinePlayer> {

    @Override
    @SuppressWarnings("deprecation")
    public OfflinePlayer parse(CommandSender sender, String source) {
        if (source.length() > 16 && source.length() != 36) {
            sender.sendMessage(Util.format("&cError: The player '&e" + source + "&c' was not found."));
            return null;
        }

        OfflinePlayer player;
        if (source.length() == 36) {
            player = Bukkit.getOfflinePlayer(UUID.fromString(source));
        } else {
            player = Bukkit.getOfflinePlayer(source);
        }

        if (player == null || (!player.hasPlayedBefore() && !player.isOnline())) {
            sender.sendMessage(Util.format("&cError: The player '&e" + source + "&c' was not found."));
            return null;
        }

        return player;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg) {
        return Lists.newArrayList(Bukkit.getOfflinePlayers()).stream().map(OfflinePlayer::getName).collect(Collectors.toList());
    }

}
