package com.ezerium.spigot.disguise;

import com.ezerium.spigot.Spigot;
import com.ezerium.spigot.utils.NMSUtil;
import com.ezerium.spigot.utils.PlayerUtils;
import com.ezerium.spigot.utils.ReflectionUtil;
import com.ezerium.spigot.utils.Util;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DisguiseHandler {

    private final List<Disguise> disguises = new ArrayList<>();

    @SneakyThrows
    public boolean disguise(@NotNull Disguise disguise) {
        Preconditions.checkNotNull(disguise, "Disguise cannot be null.");
        if (this.isDisguised(disguise.getActualUUID())) disguise.setActualUsername(this.getDisguise(disguise.getActualUUID()).getActualUsername());

        JsonObject jsonObject = PlayerUtils.getProfileSigned(disguise.getUuid());
        if (jsonObject == null) return false;

        Player player = Bukkit.getPlayer(disguise.getActualUUID());
        if (player == null) return false;

        JsonObject properties = jsonObject.getAsJsonArray("properties").get(0).getAsJsonObject();
        String texture = properties.get("value").getAsString();
        String signature = properties.get("signature").getAsString();

        Object entityPlayer = NMSUtil.getEntityPlayer(player);
        GameProfile gameProfile = (GameProfile) NMSUtil.invokeMethod(entityPlayer, "getProfile");

        ReflectionUtil.setField(gameProfile, "name", jsonObject.get("name").getAsString());

        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (Util.getVersionInt() >= 1220) {
                online.hidePlayer(Spigot.INSTANCE.getPlugin(), player);
                online.showPlayer(Spigot.INSTANCE.getPlugin(), player);
            } else {
                online.hidePlayer(player);
                online.showPlayer(player);
            }
        }

        this.disguises.add(disguise);
        player.setDisplayName(disguise.getUsername());
        player.setPlayerListName(disguise.getUsername());
        player.setCustomName(disguise.getUsername());
        player.setCustomNameVisible(true);
        return true;
    }

    @SneakyThrows
    public void clear(@NotNull Player player) {
        Preconditions.checkNotNull(player, "Player cannot be null.");
        if (!this.isDisguised(player)) return;

        Disguise disguise = this.getDisguise(player);
        if (disguise == null) return;

        this.disguises.remove(disguise);

        Disguise clearDisguise = new Disguise(disguise.getActualUUID(), disguise.getActualUsername(), disguise.getActualUUID(), disguise.getActualUsername());
        this.disguise(clearDisguise);

        this.disguises.remove(clearDisguise);
        player.setDisplayName(disguise.getActualUsername());
        player.setPlayerListName(disguise.getActualUsername());
        player.setCustomName(disguise.getActualUsername());
        player.setCustomNameVisible(false);
    }

    public boolean isDisguised(@NotNull UUID uuid) {
        Preconditions.checkNotNull(uuid, "UUID cannot be null.");
        return this.disguises.stream().anyMatch(disguise -> disguise.getActualUUID().equals(uuid));
    }

    public boolean isDisguised(@NotNull Player player) {
        Preconditions.checkNotNull(player, "Player cannot be null.");
        return this.isDisguised(player.getUniqueId());
    }

    @Nullable
    public Disguise getDisguise(@NotNull UUID uuid) {
        Preconditions.checkNotNull(uuid, "UUID cannot be null.");
        return this.disguises.stream().filter(disguise -> disguise.getActualUUID().equals(uuid)).findFirst().orElse(null);
    }

    @Nullable
    public Disguise getDisguise(@NotNull Player player) {
        Preconditions.checkNotNull(player, "Player cannot be null.");
        return this.getDisguise(player.getUniqueId());
    }

}
