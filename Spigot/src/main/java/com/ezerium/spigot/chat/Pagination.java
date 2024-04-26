package com.ezerium.spigot.chat;

import com.ezerium.spigot.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Pagination<T> {

    @Nullable
    public String getHeaderFooter(int page, int maxPage) {
        return null;
    }

    @Nullable
    public String getHeader(int page, int maxPage) {
        return null;
    }

    abstract public String format(T t, int page, int maxPage);

    @Nullable
    public String getFooter(int page, int maxPage) {
        return null;
    }

    public final void send(CommandSender sender, int page, List<T> list) {
        this.send(sender, page, 5, list);
    }

    public final void send(CommandSender sender, int page, int maxItemsPerPage, List<T> list) {
        int maxPage = (int) Math.ceil((double) list.size() / maxItemsPerPage);
        if (page < 1 || page > maxPage) {
            sender.sendMessage(Util.format("&cThe page '&e" + page + "&c' does not exist."));
            return;
        }

        String header = getHeader(page, maxPage);
        String footer = getFooter(page, maxPage);
        String headerFooter = getHeaderFooter(page, maxPage);

        if (headerFooter != null) {
            sender.sendMessage(Util.format(headerFooter));
        } else {
            if (header != null) {
                sender.sendMessage(Util.format(header));
            }
        }

        int fromIndex = (page - 1) * maxItemsPerPage;
        int toIndex = Math.min(page * maxItemsPerPage, list.size());

        for (int i = fromIndex; i < toIndex; i++) {
            sender.sendMessage(format(list.get(i), page, maxPage));
        }

        if (headerFooter != null) {
            sender.sendMessage(Util.format(headerFooter));
        } else {
            if (footer != null) {
                sender.sendMessage(Util.format(footer));
            }
        }
    }

}
