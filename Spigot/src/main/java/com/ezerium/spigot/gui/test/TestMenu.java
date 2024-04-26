package com.ezerium.spigot.gui.test;

import com.ezerium.spigot.gui.Menu;
import com.ezerium.spigot.gui.button.Button;
import com.ezerium.spigot.gui.button.impl.GlassButton;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMenu extends Menu {

    @NotNull
    @Override
    public String getTitle(Player player) {
        return "Test GUI";
    }

    @Override
    public int getSize(Player player) {
        return 9 * 3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            buttons.put(i, new GlassButton());
        }

        buttons.put(10, new Button() {
            @NotNull
            @Override
            public String getName(Player player) {
                return "&2Test Button";
            }

            @NotNull
            @Override
            public List<String> getLore(Player player) {
                return Lists.newArrayList("Test lore line 1", "&aTest lore line 2");
            }

            @NotNull
            @Override
            public Material getMaterial(Player player) {
                return Material.DIAMOND;
            }

            @Override
            public void click(Player player, ClickType clickType, int slot) {
                player.sendMessage("You clicked the test button!");
                player.sendMessage("Click type: " + clickType.name());
                player.sendMessage("Slot: " + slot);
            }
        });

        return buttons;
    }
}
