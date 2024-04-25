package com.ezerium.spigot.gui.button.impl;

import com.ezerium.spigot.gui.button.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GlassButton extends Button {

    @NotNull
    @Override
    public Material getMaterial(Player player) {
        return Material.STAINED_GLASS_PANE;
    }

    @Override
    public byte getDamage(Player player) {
        return 15;
    }

    @NotNull
    @Override
    public String getName(Player player) {
        return " ";
    }

    @NotNull
    @Override
    public List<String> getLore(Player player) {
        return new ArrayList<>();
    }

    @Override
    public void click(Player player, ClickType clickType, int slot) {

    }
}
