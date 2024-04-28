package com.ezerium.spigot.npc.events;

import com.ezerium.spigot.npc.EzNPC;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Data
@AllArgsConstructor
public class NPCSpawnEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final EzNPC npc;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
