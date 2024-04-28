package com.ezerium.spigot.npc.listener;

import com.ezerium.spigot.npc.EzNPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NPCListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();


        EzNPC.getNpcs().values().forEach(npc -> {
            if (npc.isSpawned()) {
                npc.show(player);
            }
        });
    }
}
