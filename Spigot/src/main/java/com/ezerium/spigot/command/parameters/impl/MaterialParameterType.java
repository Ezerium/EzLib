package com.ezerium.spigot.command.parameters.impl;

import com.ezerium.spigot.command.parameters.ParameterType;
import com.ezerium.spigot.utils.Util;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialParameterType implements ParameterType<Material> {

    @Override
    public Material parse(CommandSender sender, String source) {
        Material material = Material.matchMaterial(source);
        if (material == null) {
            sender.sendMessage(Util.format("&cError: The material '&e" + source + "&c' was not found."));
            return null;
        }

        return material;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg) {
        return Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList());
    }
}
