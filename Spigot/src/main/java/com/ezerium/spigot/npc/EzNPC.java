package com.ezerium.spigot.npc;

import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.ezerium.spigot.Spigot;
import com.ezerium.spigot.hologram.Hologram;
import com.ezerium.spigot.npc.events.NPCSpawnEvent;
import com.ezerium.spigot.utils.NMSUtil;
import com.ezerium.spigot.utils.PlayerUtils;
import com.ezerium.spigot.utils.Util;
import com.ezerium.utils.LoggerUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.*;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

@Getter
public class EzNPC {

    private static final Map<String, EzNPC> npcs = new HashMap<>();
    /*public static Object TEAM;

    static {
        try {
            Class<?> scoreboard = Util.getNMSClass("ScoreboardTeam");
            Class<?> enumNameTagVisibility = Util.getNMSClass("ScoreboardTeamBase$EnumNameTagVisibility");
            // ((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle() in reflection
            Class<?> craftScoreboard = Util.getOBCClass("scoreboard.CraftScoreboard");
            Object sb = craftScoreboard.cast(Bukkit.getScoreboardManager().getMainScoreboard());
            Object nmsScoreboard = craftScoreboard.getDeclaredMethod("getHandle").invoke(sb);
            TEAM = scoreboard.getConstructor(Util.getNMSClass("Scoreboard"), String.class)
                    .newInstance(nmsScoreboard, "NPCs");
            TEAM.getClass().getMethod("setNameTagVisibility", enumNameTagVisibility).invoke(TEAM, enumNameTagVisibility.getField("NEVER").get(null));
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.err("Something went wrong while trying to create the NPC team.");
        }
    }*/

    public static Map<String, EzNPC> getNpcs() {
        return npcs == null ? new HashMap<>() : npcs;
    }

    public static Team TEAM;

    static {
        TEAM = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("NPCs");
        if (TEAM != null) TEAM.unregister();
        TEAM = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("NPCs");
        TEAM.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        TEAM.setColor(ChatColor.GRAY);
        TEAM.setPrefix(ChatColor.GRAY + "[NPC] ");
    }

    private final String id;
    @Setter
    private UUID uuid;
    @Setter
    private Callable<List<String>> lines;

    @Setter
    private Location location;
    private boolean spawned;
    private boolean autoUpdate;

    private Hologram hologram;
    private GameProfile profile;
    private Object entityPlayer;
    private int entityId;
    @Setter
    private String texture;
    @Setter
    private String signature;

    @Setter
    private NPCInteract onInteract;

    public EzNPC(String id, UUID uuid, Callable<List<String>> lines, Location location, NPCInteract onInteract, boolean spawned, String texture, String signature) {
        this.id = id;
        this.uuid = uuid;
        this.lines = lines;
        this.location = location;
        this.spawned = spawned;
        this.autoUpdate = true;
        this.onInteract = onInteract;

        this.profile = new GameProfile(uuid, id);

        if (texture != null && signature != null) {
            this.texture = texture;
            this.signature = signature;
        } else {
            JsonObject textures = PlayerUtils.getProfileSigned(uuid);
            if (textures != null) {
                textures = textures.getAsJsonArray("properties").get(0).getAsJsonObject();
                this.texture = textures.get("value").getAsString();
                this.signature = textures.get("signature").getAsString();
            }
        }

        this.setSkin(false);
        this.entityPlayer = NMSUtil.createEntityPlayer(NMSUtil.getMCServer(), NMSUtil.getWorldServer(location.getWorld()), profile, location);
        this.entityId = (int) NMSUtil.invokeMethod(this.entityPlayer, "getId");

        this.hologram = new Hologram(id + "_hologram", this.lines, location, true);
    }

    public EzNPC(String id, UUID uuid, Callable<List<String>> lines, Location location, NPCInteract onInteract, boolean spawned) {
        this(id, uuid, lines, location, onInteract, spawned, null, null);
    }

    public EzNPC(String id, UUID uuid, Callable<List<String>> lines, Location location, NPCInteract onInteract) {
        this(id, uuid, lines, location, onInteract, false);
    }

    public EzNPC(String id, UUID uuid, Location location, NPCInteract onInteract) {
        this(id, uuid, () -> Lists.newArrayList(id), location, onInteract);
    }

    public EzNPC(String id, UUID uuid, Location location) {
        this(id, uuid, location, (p, a) -> {});
    }

    public EzNPC(String id, Callable<List<String>> lines, Location location, NPCInteract onInteract) {
        this(id, PlayerUtils.getUUID(id) == null ? UUID.randomUUID() : PlayerUtils.getUUID(id), lines, location, onInteract);
    }

    public EzNPC(String id, Location location, NPCInteract onInteract) {
        this(id, PlayerUtils.getUUID(id) == null ? UUID.randomUUID() : PlayerUtils.getUUID(id), location, onInteract);
    }

    public EzNPC(String id, Location location) {
        this(id, PlayerUtils.getUUID(id) == null ? UUID.randomUUID() : PlayerUtils.getUUID(id), location);
    }

    public final void teleport(Location location) {
        this.location = location;
        this.hologram.setLocation(location);
        NMSUtil.invokeMethod(this.entityPlayer, "setLocation", location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public final void setSkin(UUID uuid) {
        JsonObject textures = PlayerUtils.getProfileSigned(uuid);
        if (textures != null) {
            textures = textures.getAsJsonArray("properties").get(0).getAsJsonObject();
            this.setSkin(textures.get("value").getAsString(), textures.get("signature").getAsString());
        }
    }

    public final void setSkin(String texture, String signature) {
        this.texture = texture;
        this.signature = signature;

        this.profile.getProperties().clear();
        this.profile.getProperties().put("textures", new Property("textures", texture, signature));
    }

    public final void setSkin(boolean autoChangeTextures) {
        if (autoChangeTextures) this.setSkin(this.uuid);
        else this.setSkin(this.texture, this.signature);
    }

    public final void setSkin() {
        this.setSkin(true);
    }

    public final void spawn() {
        Preconditions.checkState(!this.spawned, "NPC is already spawned.");
        Preconditions.checkState(!npcs.containsKey(id), "NPC with id " + id + " already exists.");
        npcs.put(id, this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.show(player);
        }

        TEAM.addEntry(id);

        this.hologram.spawn();
        this.spawned = true;

        Bukkit.getPluginManager().callEvent(new NPCSpawnEvent(this));
    }

    @SneakyThrows
    public final void show(Player player) {
        /*try {
            Class<?> packetPlayOutPlayerInfo = Util.getNMSClass("PacketPlayOutPlayerInfo");
            Class<?> packetPlayOutNamedEntitySpawn = Util.getNMSClass("PacketPlayOutNamedEntitySpawn");
            Class<?> packetPlayOutEntityHeadRotation = Util.getNMSClass("PacketPlayOutEntityHeadRotation");
            Class<?> enumPlayerInfoAction = Util.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            Class<?> entityPlayerArray = Class.forName("[Lnet.minecraft.server." + Util.getNMSVersion() + ".EntityPlayer;");

            Object array = Array.newInstance(Util.getNMSClass("EntityPlayer"), 1);
            Array.set(array, 0, this.entityPlayer);

            LoggerUtil.warn("Array: " + array);

            Object playerInfoAdd = packetPlayOutPlayerInfo.getConstructor(enumPlayerInfoAction, entityPlayerArray)
                    .newInstance(enumPlayerInfoAction.getField("ADD_PLAYER").get(null), array);
            LoggerUtil.warn("PlayerInfoAdd: " + playerInfoAdd);
            Object namedEntitySpawn = packetPlayOutNamedEntitySpawn.getConstructor(Util.getNMSClass("EntityHuman"))
                    .newInstance(this.entityPlayer);
            LoggerUtil.warn("NamedEntitySpawn: " + namedEntitySpawn);
            float yaw = (float) this.entityPlayer.getClass().getField("yaw").get(this.entityPlayer);
            LoggerUtil.warn("Yaw: " + yaw);
            Object entityHeadRotation = packetPlayOutEntityHeadRotation.getConstructor(Util.getNMSClass("Entity"), byte.class)
                    .newInstance(this.entityPlayer, (byte) ((int) yaw * 256.0F / 360.0F));
            LoggerUtil.warn("EntityHeadRotation: " + entityHeadRotation);
            Object playerInfoRemove = packetPlayOutPlayerInfo.getConstructor(enumPlayerInfoAction, entityPlayerArray)
                    .newInstance(enumPlayerInfoAction.getField("REMOVE_PLAYER").get(null), array);
            LoggerUtil.warn("PlayerInfoRemove: " + playerInfoRemove);

            NMSUtil.sendPacket(player, playerInfoAdd);
            LoggerUtil.warn("1");
            NMSUtil.sendPacket(player, namedEntitySpawn);
            LoggerUtil.warn("2");
            //NMSUtil.sendPacket(player, entityHeadRotation);
            LoggerUtil.warn("3");
            Bukkit.getScheduler().runTaskLaterAsynchronously(Spigot.INSTANCE.getPlugin(), () -> {
                NMSUtil.sendPacket(player, playerInfoRemove);
            }, 20L);

            /*Class<?> outScoreboardTeam = Util.getNMSClass("PacketPlayOutScoreboardTeam");
            Object packet1 = outScoreboardTeam.getConstructor(TEAM.getClass(), int.class).newInstance(TEAM, 1);
            LoggerUtil.warn("Packet1: " + packet1);
            Object packet2 = outScoreboardTeam.getConstructor(TEAM.getClass(), int.class).newInstance(TEAM, 0);
            LoggerUtil.warn("Packet2: " + packet2);
            TypeToken<Collection<String>> type = new TypeToken<Collection<String>>() {};
            Object packet3 = outScoreboardTeam.getConstructor(TEAM.getClass(), type.getRawType(), int.class).newInstance(TEAM, new ArrayList<String>() {{
                npcs.values().stream().map(EzNPC::getId).forEach((id) -> {
                    if (!contains(id)) add(id);
                });
                LoggerUtil.warn(this);
            }}, 3);
            LoggerUtil.warn("Packet3: " + packet3);

            NMSUtil.sendPacket(player, packet1);
            LoggerUtil.warn("5");
            NMSUtil.sendPacket(player, packet2);
            LoggerUtil.warn("6");
            NMSUtil.sendPacket(player, packet3);
            LoggerUtil.warn("7");* /
        } catch (Exception e) {
            LoggerUtil.err("Something went wrong while trying to show NPC '" + this.id + "' to player '" + player.getName() + "'.");
            LoggerUtil.err("Caused by: " + e.getCause().toString());
            e.printStackTrace();
        }*/

        PacketContainer spawn = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        PacketContainer preSpawn = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        PacketContainer removePacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);

        WrapperPlayServerNamedEntitySpawn body = new WrapperPlayServerNamedEntitySpawn(spawn);

        int entityId = (int) this.entityPlayer.getClass().getMethod("getId").invoke(this.entityPlayer);
        body.setEntityID(entityId);

        body.setPosition(location.getDirection());
        body.setPlayerUUID(uuid);
        body.setPitch(location.getPitch());
        body.setYaw(location.getYaw());

        body.setX(location.getX());
        body.setY(location.getY());
        body.setZ(location.getZ());

        WrapperPlayServerPlayerInfo preBody = new WrapperPlayServerPlayerInfo(preSpawn);
        preBody.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        WrappedGameProfile profile = getWrappedProfile();
        PlayerInfoData data = new PlayerInfoData(profile, 10, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(this.id));

        ArrayList<PlayerInfoData> dataList = new ArrayList<>();
        dataList.add(data);
        WrappedDataWatcher.Serializer toFloat = WrappedDataWatcher.Registry.get(Float.class);

        preBody.setData(dataList);

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject object = new WrappedDataWatcher.WrappedDataWatcherObject(7, toFloat);
        watcher.setObject(object, 20F);

        body.setMetadata(watcher);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.sendServerPacket(player, preSpawn);
        manager.sendServerPacket(player, spawn);
    }

    @SneakyThrows
    public final void hide(Player player) {
        PacketContainer removePacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        WrapperPlayServerPlayerInfo remove = new WrapperPlayServerPlayerInfo(removePacket);
        remove.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        WrappedGameProfile profile = getWrappedProfile();
        PlayerInfoData data = new PlayerInfoData(profile, 10, EnumWrappers.NativeGameMode.CREATIVE, WrappedChatComponent.fromText(this.id));

        ArrayList<PlayerInfoData> dataList = new ArrayList<>();
        dataList.add(data);

        remove.setData(dataList);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.sendServerPacket(player, removePacket);
    }

    private WrappedGameProfile getWrappedProfile() {
        WrappedGameProfile profile = new WrappedGameProfile(this.uuid, this.id);
        profile.getProperties().put("textures", new WrappedSignedProperty("textures", this.texture, this.signature));
        return profile;
    }

    @SneakyThrows
    public final void despawn() {
        this.spawned = false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.hide(player);
        }

        this.hologram.despawn();
        npcs.remove(id);
    }
}
