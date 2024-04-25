package com.ezerium.spigot;

import com.ezerium.inject.InjectHandler;
import com.ezerium.spigot.chat.ChatInputListener;
import com.ezerium.spigot.gui.listener.MenuListener;
import com.ezerium.spigot.inject.impl.InjectPluginField;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public final class Spigot {

    public static Spigot INSTANCE;

    @Getter
    private final JavaPlugin plugin;

    @Getter
    private final Config config;

    public Spigot(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = new Config();

        this.init();
    }

    private void init() {
        InjectHandler.addInjector(new InjectPluginField());
        for (Field field : plugin.getClass().getDeclaredFields()) {
            InjectHandler.inject(field);
        }

        INSTANCE = this;

        this.plugin.getServer().getPluginManager().registerEvents(new ChatInputListener(), this.plugin);
        this.plugin.getServer().getPluginManager().registerEvents(new MenuListener(), this.plugin);
    }

}
