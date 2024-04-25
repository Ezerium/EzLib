package com.ezerium.spigot.command.parameters.impl;

import com.ezerium.spigot.command.parameters.ParameterType;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class FloatParameterType implements ParameterType<Float> {

    @Override
    public Float parse(CommandSender sender, String source) {
        if (source.toLowerCase().contains("e")) {
            sender.sendMessage("Error: Scientific notation is not supported.");
            return null;
        }

        try {
            float value = Float.parseFloat(source);
            if (Float.isInfinite(value) || Float.isNaN(value)) {
                sender.sendMessage("Error: '" + source + "' is not a valid number.");
                return null;
            }

            return value;
        } catch (NumberFormatException e) {
            sender.sendMessage("Error: '" + source + "' is not a valid number.");
            return null;
        }
    }
}
