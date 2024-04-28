package com.ezerium.spigot.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface NPCInteract {

    void onInteract(Player player, NPCAction action);

}
