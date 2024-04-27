package com.ezerium.spigot;

import com.ezerium.inject.InjectHandler;
import com.ezerium.spigot.chat.ChatInputListener;
import com.ezerium.spigot.gui.listener.MenuListener;
import com.ezerium.spigot.inject.impl.InjectPluginField;
import com.ezerium.spigot.scoreboard.Scoreboard;
import com.ezerium.spigot.scoreboard.ScoreboardLine;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

public final class Spigot implements Listener {

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
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ScoreboardLine line1 = new ScoreboardLine(p -> "name: " + p.getName(), true);

        Scoreboard scoreboard = new Scoreboard("Test Scoreboard", "Line 1", "Line 2", "Line 3");
        scoreboard.addLine(line1);
        scoreboard.display(player);
    }

}
