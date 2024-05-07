package com.ezerium.spigot.gui.button.impl;

import com.ezerium.spigot.gui.PaginatedMenu;
import com.ezerium.spigot.gui.button.Button;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AllArgsConstructor
public class PreviousButton extends Button {

    private final PaginatedMenu menu;

    @NotNull
    @Override
    public String getName(Player player) {
        return "&aPrevious Page";
    }

    @NotNull
    @Override
    public List<String> getLore(Player player) {
        return Lists.newArrayList("&7Click to go to the previous page.");
    }

    @NotNull
    @Override
    public Material getMaterial(Player player) {
        return Material.ARROW;
    }

    @Override
    public void click(Player player, ClickType clickType, int slot) {
        menu.setPage(menu.getPage() - 1);
        player.closeInventory();
        menu.open(player);
    }
}
