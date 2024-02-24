package com.ezerium.spigot.command.parameters.impl;

import com.ezerium.spigot.command.parameters.ParameterType;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class BooleanParameterType implements ParameterType<Boolean> {

    private static final String[] TRUE_VALUES = new String[] { "true", "yes", "on", "1" };
    private static final String[] FALSE_VALUES = new String[] { "false", "no", "off", "0" };

    @Override
    public Boolean parse(CommandSender sender, String source) {
        if (Arrays.asList(TRUE_VALUES).contains(source.toLowerCase())) {
            return true;
        } else if (Arrays.asList(FALSE_VALUES).contains(source.toLowerCase())) {
            return false;
        }

        return null;
    }

}
