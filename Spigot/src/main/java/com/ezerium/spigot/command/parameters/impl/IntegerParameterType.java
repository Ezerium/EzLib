package com.ezerium.spigot.command.parameters.impl;

import com.ezerium.spigot.command.parameters.ParameterType;
import com.ezerium.spigot.utils.Util;
import org.bukkit.command.CommandSender;

public class IntegerParameterType implements ParameterType<Integer> {

    @Override
    public Integer parse(CommandSender sender, String source) {
        try {
            return Integer.parseInt(source);
        } catch (NumberFormatException e) {
            sender.sendMessage(Util.format("&cError: '&e" + source + "&c' is not a valid number."));
            return null;
        }
    }

}
