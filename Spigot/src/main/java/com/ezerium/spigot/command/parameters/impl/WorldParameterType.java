package com.ezerium.spigot.command.parameters.impl;

import com.ezerium.spigot.command.parameters.ParameterType;
import com.ezerium.spigot.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class WorldParameterType implements ParameterType<World> {

    @Override
    public World parse(CommandSender sender, String source) {
        if (source.equalsIgnoreCase("@s")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.format("&cOnly players can use @s."));
                return null;
            }

            return ((Player) sender).getWorld();
        }

        World world = Bukkit.getWorld(source);
        if (world == null) {
            sender.sendMessage(Util.format("&cError: The world '&e" + source + "&c' was not found."));
            return null;
        }

        return world;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg) {
        return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
    }
}
