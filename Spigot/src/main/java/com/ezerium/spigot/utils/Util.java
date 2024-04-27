package com.ezerium.spigot.utils;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class Util {

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String format(String message, Object... args) {
        return String.format(format(message), args);
    }

    public static String[] format(String[] messages) {
        for (int i = 0; i < messages.length; i++) {
            messages[i] = format(messages[i]);
        }
        return messages;
    }

    public static String[] format(String[] messages, Object... args) {
        for (int i = 0; i < messages.length; i++) {
            messages[i] = format(messages[i], args);
        }

        return messages;
    }

    public static List<String> format(List<String> messages) {
        return Lists.newArrayList(format(messages.toArray(new String[0])));
    }

    public static List<String> format(List<String> messages, Object... args) {
        return Lists.newArrayList(format(messages.toArray(new String[0]), args));
    }

    public static String getNMSVersion() {
        String v = Bukkit.getServer().getClass().getPackage().getName();
        v = v.substring(v.lastIndexOf('.') + 1);

        return v;
    }

    public static String getOBCVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[2];
    }

    public static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getImplementationVersion();
    }

    public static int getVersionInt() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        if (version.split("\\.").length == 2) version += ".0";
        return Integer.parseInt(version.replace(".", ""));
    }

    @Nullable
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getNMSVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static Class<?> getOBCClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getNMSVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
