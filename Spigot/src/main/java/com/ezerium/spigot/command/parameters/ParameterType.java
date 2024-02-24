package com.ezerium.spigot.command.parameters;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public interface ParameterType<T> {

    T parse(CommandSender sender, String source);

    default List<String> tabComplete(CommandSender sender, String arg) {
        return new ArrayList<>();
    }

}
