package com.ezerium.spigot.gui;

import com.ezerium.spigot.Spigot;
import com.ezerium.spigot.gui.button.Button;
import com.ezerium.spigot.utils.Util;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class Menu {

    public final static Map<UUID, Menu> OPENED_MENUS = new HashMap<>();

    @Nullable
    private BukkitTask task = null;

    @Setter
    private boolean update = false;
    @Setter
    private long updateTicks = 20L;

    @NotNull
    abstract public String getTitle(Player player);

    abstract public int getSize(Player player);

    @Nullable
    public InventoryType getInventoryType(Player player) {
        return null;
    }

    abstract public Map<Integer, Button> getButtons(Player player);

    public void onClose(Player player) {
    }

    public void onOpen(Player player) {
    }

    public void open(Player player) {
        InventoryType inventoryType = getInventoryType(player);
        Inventory inventory;
        if (inventoryType != null) {
            inventory = Bukkit.createInventory(player, inventoryType, Util.format(getTitle(player)));
        } else {
            inventory = Bukkit.createInventory(player, getSize(player), Util.format(getTitle(player)));
        }

        OPENED_MENUS.put(player.getUniqueId(), this);

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!OPENED_MENUS.containsKey(player.getUniqueId())) {
                    cancel();
                }

                Map<Integer, Button> buttons = getButtons(player);
                for (int slot : buttons.keySet()) {
                    Button button = buttons.get(slot);
                    inventory.setItem(slot, button.build(player));
                }
            }
        };

        if (update) {
            this.task = runnable.runTaskTimerAsynchronously(Spigot.INSTANCE.getPlugin(), 0L, updateTicks);
        } else {
            runnable.run();
        }

        player.openInventory(inventory);
        onOpen(player);
    }

    public final void remove(Player player) {
        if (task != null) {
            task.cancel();
            task = null;
        }

        onClose(player);
        OPENED_MENUS.remove(player.getUniqueId());
    }
}
