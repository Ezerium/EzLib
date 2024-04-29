package com.ezerium.spigot.hologram;

import com.ezerium.spigot.Spigot;
import com.google.common.base.Preconditions;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Function;

public class Hologram {

    @Getter
    private static final Map<String, Hologram> holograms = new HashMap<>();

    @Getter
    private final String id;
    @Getter
    @Setter
    private Callable<List<String>> lines;
    @Getter
    private Location location;

    @Getter
    @Setter
    private boolean spawned;
    @Getter
    @Setter
    private boolean autoUpdate;
    @Getter
    @Setter
    private double maxLinesDepth;

    private BukkitTask task;
    private List<ArmorStand> armorStands;

    public Hologram(String id, Callable<List<String>> lines, Location location, boolean autoUpdate) {
        this.id = id;
        this.lines = lines;
        this.location = location;
        this.autoUpdate = autoUpdate;
        this.maxLinesDepth = 0.25D;

        this.armorStands = new ArrayList<>();
    }

    public Hologram(String id, Callable<List<String>> lines, Location location) {
        this(id, lines, location, true);
    }

    public final void spawn() {
        Preconditions.checkState(!holograms.containsKey(this.id), "Hologram with id '" + this.id + "' already exists.");
        if (this.spawned) return;

        Runnable runnable = this::updateLines;

        Bukkit.getScheduler().runTaskLater(Spigot.INSTANCE.getPlugin(), runnable, 5L);
        if (this.autoUpdate) {
            this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(Spigot.INSTANCE.getPlugin(), runnable, 20L, 20L);
        }

        holograms.put(this.id, this);
        this.spawned = true;
    }

    public final void setLocation(Location location) {
        this.location = location;
        if (this.spawned) {
            this.updateLines();
        }
    }

    private void updateLines() {
        try {
            List<String> lines = this.lines.call();
            if (lines == null) return;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line == null) continue;

                ArmorStand armorStand;
                if (this.armorStands.size() <= i) {
                    armorStand = this.location.getWorld().spawn(this.location, ArmorStand.class);
                    this.armorStands.add(armorStand);
                }
            }


            int i = 0;
            for (ArmorStand armorStand : this.armorStands) {
                armorStand.setCustomName(lines.get(i));
                armorStand.setCustomNameVisible(true);
                armorStand.setGravity(false);
                armorStand.setVisible(false);
                armorStand.setCollidable(false);
                armorStand.setInvulnerable(true);

                double y = -this.maxLinesDepth + (this.maxLinesDepth * i);
                armorStand.teleport(this.location.clone().add(0.0D, y, 0.0D));

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void despawn() {
        if (!this.spawned) return;

        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        for (ArmorStand armorStand : this.armorStands) {
            armorStand.remove();
        }

        holograms.remove(this.id);
        this.spawned = false;
    }

}
