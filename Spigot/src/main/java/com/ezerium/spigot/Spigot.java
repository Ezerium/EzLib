package com.ezerium.spigot;

//import com.comphenix.packetwrapper.wrappers.play.serverbound.WrapperPlayClientUseEntity;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import com.ezerium.annotations.Async;
import com.ezerium.http.HTTPRequest;
import com.ezerium.inject.InjectHandler;
import com.ezerium.spigot.chat.ChatInputListener;
import com.ezerium.spigot.gui.listener.MenuListener;
import com.ezerium.spigot.hologram.Hologram;
import com.ezerium.spigot.inject.impl.InjectPluginField;
import com.ezerium.spigot.npc.EzNPC;
import com.ezerium.spigot.npc.NPCAction;
import com.ezerium.spigot.npc.listener.NPCListener;
import com.ezerium.spigot.scoreboard.Scoreboard;
import com.ezerium.spigot.scoreboard.ScoreboardLine;
import com.ezerium.utils.LoggerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class Spigot implements Listener {

    public static final int VERSION_CODE = 1;

    public static Spigot INSTANCE;

    @Getter
    private final JavaPlugin plugin;

    @Getter
    private final Config config;
    @Getter
    private final ServerStats serverStats;

    public Spigot(JavaPlugin plugin, boolean versionCheck) {
        this.plugin = plugin;
        this.config = new Config();
        this.serverStats = new ServerStats(plugin);

        if (versionCheck) {
            //this.versionCheck();
        }

        this.init();
    }

    public Spigot(JavaPlugin plugin) {
        this(plugin, true);
    }

    @Async
    private void versionCheck() {
        HTTPRequest request = new HTTPRequest("https://api.ezerium.com/api/v1/ezlib/version");
        request.fetch((obj) -> {
            String version = obj.get("version").getAsString();
            int versionCode = obj.get("versionCode").getAsInt();

            if (versionCode > VERSION_CODE) {
                LoggerUtil.warn("A new version of EzLib is available! Version: " + version);
            }
        });
    }

    /**
     * Remember to add this in your onDisable method,
     * or you could experience issues.
     */
    public void disable() {
        EzNPC.getNpcs().forEach((id, npc) -> npc.despawn());
        EzNPC.getNpcs().clear();

        Hologram.getHolograms().forEach((id, hologram) -> hologram.despawn());
        Hologram.getHolograms().clear();
    }

    private void init() {
        InjectHandler.addInjector(new InjectPluginField());
        for (Field field : plugin.getClass().getDeclaredFields()) {
            InjectHandler.inject(field);
        }

        INSTANCE = this;

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() != PacketType.Play.Client.USE_ENTITY) return;

                PacketContainer packet = event.getPacket();

                Player player = event.getPlayer();
                int entityId = packet.getIntegers().read(0);

                EzNPC ezNPC = EzNPC.getNpcs().values().stream().filter(npc -> npc.getEntityId() == entityId).findFirst().orElse(null);
                if (ezNPC != null) {
                    EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);
                    StructureModifier<EnumWrappers.Hand> hands = packet.getHands();
                    EnumWrappers.Hand hand;
                    try {
                        hand = hands.read(0);
                    } catch (Exception e) {
                        hand = EnumWrappers.Hand.MAIN_HAND;
                    }

                    boolean isShift = player.isSneaking();
                    if (hand == EnumWrappers.Hand.OFF_HAND) return;

                    switch (action) {
                        case INTERACT_AT:
                            ezNPC.getOnInteract().onInteract(player, (isShift ? NPCAction.SHIFT_RIGHT_CLICK : NPCAction.RIGHT_CLICK));
                            break;
                        case ATTACK:
                            ezNPC.getOnInteract().onInteract(player, (isShift ? NPCAction.SHIFT_LEFT_CLICK : NPCAction.LEFT_CLICK));
                            break;
                    }
                }
            }
        });

        this.plugin.getServer().getPluginManager().registerEvents(new ChatInputListener(), this.plugin);
        this.plugin.getServer().getPluginManager().registerEvents(new MenuListener(), this.plugin);
        this.plugin.getServer().getPluginManager().registerEvents(new NPCListener(), this.plugin);
        //this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ScoreboardLine line1 = new ScoreboardLine(p -> "name: " + p.getName(), true);

        Scoreboard scoreboard = new Scoreboard("Test Scoreboard", "Line 1", "Line 2", "Line 3");
        scoreboard.addLine(line1);
        scoreboard.display(player);
    }

}
