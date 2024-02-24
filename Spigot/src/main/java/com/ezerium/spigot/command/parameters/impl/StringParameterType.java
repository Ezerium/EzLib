package com.ezerium.spigot.command.parameters.impl;

import com.ezerium.spigot.command.parameters.ParameterType;
import org.bukkit.command.CommandSender;

public class StringParameterType implements ParameterType<String> {

    @Override
    public String parse(CommandSender sender, String source) {
        return source;
    }
}
