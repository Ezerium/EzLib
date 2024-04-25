package com.ezerium.spigot.actionbar;

import com.ezerium.spigot.Spigot;
import com.ezerium.spigot.utils.Util;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@Data
@AllArgsConstructor
public class ActionBar {

    private String message;
    private boolean stay;

    public final void send(Player player) {
        Preconditions.checkNotNull(player, "Player cannot be null.");
        Preconditions.checkNotNull(message, "Message cannot be null.");
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Util.format(message)));
                if (!stay) this.cancel();
            }
        };
        runnable.run();

        if (stay) runnable.runTaskTimerAsynchronously(Spigot.INSTANCE.getPlugin(), 20L, 20);
    }

}
