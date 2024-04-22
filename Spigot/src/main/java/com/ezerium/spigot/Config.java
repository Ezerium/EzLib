package com.ezerium.spigot;

import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

@Data
public class Config {

    private String primaryColor = "&3";
    private String secondaryColor = "&b";
    private String errorColor = "&c";

    private String noPermission = "&cYou do not have permission to execute this command.";
    private String playerOnly = "&cThis command can only be executed by a player.";
    private String consoleOnly = "&cThis command can only be executed by the console.";
    private String invalidUsage = "&cUsage: %s";
    private String invalidUsageList = "&7&m------------------&r\n%s\n&7&m------------------&r";
    private String onCooldown = "&cYou are on cooldown for %s.";
    private String unknownCommand = getUnknownCommand();

    @SneakyThrows
    private String getSpigotUnknownCommand() {
        Class<?> clazz = Class.forName("org.spigotmc.SpigotConfig");
        Field field = clazz.getDeclaredField("unknownCommandMessage");
        field.setAccessible(true);

        return (String) field.get(clazz);
    }

}
