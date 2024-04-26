package com.ezerium.spigot;

import com.ezerium.spigot.annotations.Plugin;
import com.ezerium.spigot.command.CommandHandler;
import com.ezerium.spigot.command.test.TestCommands;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of the Spigot module.
 * Testing purposes only.
 */
@Plugin(name = "EzLib", version = "1.0.0", authors = { "grcq", "IDuckz_" }, description = "A Java library.")
public class Main extends JavaPlugin {

    private Spigot spigot;

    @Override
    public void onEnable() {
        this.spigot = new Spigot(this);
        CommandHandler commandHandler = new CommandHandler();
        //commandHandler.registerAll(this);
        commandHandler.register(TestCommands.class);
    }

    @Override
    public void onDisable() {
        this.spigot = null;
    }
}
