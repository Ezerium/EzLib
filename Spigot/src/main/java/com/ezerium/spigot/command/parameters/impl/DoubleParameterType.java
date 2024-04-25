package com.ezerium.spigot.command.parameters.impl;

import com.ezerium.spigot.command.parameters.ParameterType;
import org.bukkit.command.CommandSender;

public class DoubleParameterType implements ParameterType<Double> {

    @Override
    public Double parse(CommandSender sender, String source) {
        if (source.toLowerCase().contains("e")) {
            sender.sendMessage("Error: Scientific notation is not supported.");
            return null;
        }

        try {
            double value = Double.parseDouble(source);
            if (Double.isInfinite(value) || Double.isNaN(value)) {
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
