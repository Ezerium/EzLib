package com.ezerium.spigot;

import com.ezerium.shared.inject.InjectHandler;
import com.ezerium.spigot.inject.impl.InjectPluginField;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public final class Spigot {

    @Getter
    private final JavaPlugin plugin;

    public Spigot(JavaPlugin plugin) {
        this.plugin = plugin;

        this.init();
    }

    private void init() {
        InjectHandler.addInjector(new InjectPluginField());
        for (Field field : plugin.getClass().getDeclaredFields()) {
            InjectHandler.inject(field);
        }
    }

}
