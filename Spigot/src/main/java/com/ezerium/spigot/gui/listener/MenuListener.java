package com.ezerium.spigot.gui.listener;

import com.ezerium.spigot.gui.Menu;
import com.ezerium.spigot.gui.button.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Menu menu = Menu.OPENED_MENUS.get(player.getUniqueId());
        if (menu == null) return;

        Map<Integer, Button> buttons = menu.getButtons(player);
        if (buttons == null) return;

        Button button = buttons.get(event.getSlot());
        if (button == null) return;

        event.setCancelled(button.cancelClick(player));
        button.click(player, event.getClick(), event.getSlot());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        Menu menu = Menu.OPENED_MENUS.get(player.getUniqueId());
        if (menu == null) return;

        BukkitTask task = menu.getTask();
        if (task != null) {
            task.cancel();
        }

        menu.remove(player);
    }

}
