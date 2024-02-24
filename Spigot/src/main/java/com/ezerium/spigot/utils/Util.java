package com.ezerium.spigot.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class Util {

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
