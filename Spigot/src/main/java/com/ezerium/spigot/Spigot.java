package com.ezerium.spigot;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spigot {

    @Getter
    private final JavaPlugin plugin;

    public Spigot(JavaPlugin plugin) {
        this.plugin = plugin;

        this.init();
    }

    private void init() {
        // TODO: Scan for @Inject annotations and insert values for each manager and/or handler.
    }

}
