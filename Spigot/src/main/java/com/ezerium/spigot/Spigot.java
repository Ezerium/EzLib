package com.ezerium.spigot;

import com.ezerium.shared.inject.InjectHandler;
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
    }

}
