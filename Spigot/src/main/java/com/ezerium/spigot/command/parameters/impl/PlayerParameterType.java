package com.ezerium.spigot.command.parameters.impl;

import com.ezerium.spigot.command.parameters.ParameterType;
import com.ezerium.spigot.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerParameterType implements ParameterType<Player> {

    @Override
    public Player parse(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equals("@p") || source.equals("@s"))) {
            return (Player) sender;
        }

        Player player = Bukkit.getPlayer(source);
        if (player != null) {
            return player;
        }

        sender.sendMessage(Util.format("&cError: Player '&e" + source + "&c' not found."));
        return null;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}
