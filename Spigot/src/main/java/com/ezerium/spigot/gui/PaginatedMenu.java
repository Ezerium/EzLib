package com.ezerium.spigot.gui;

import com.ezerium.spigot.gui.button.Button;
import com.google.common.base.Preconditions;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PaginatedMenu extends Menu {

    @Setter
    private int page;

    public PaginatedMenu(int page) {
        super();

        this.page = page;
        Preconditions.checkArgument(page > 0, "Page must be greater than 0.");
    }

    abstract public String getTitle(Player player, int page);

    /**
     * Get the number of items per page. Must be counter by per 9.
     * @param player The player to get the items per page for.
     * @return The number of items per page.
     */
    abstract public int getItemsPerPage(Player player);

    abstract public List<Button> getItems(Player player);

    @Nullable
    public Comparator<? super Button> sortBy(Player player) {
        return null;
    }

    @NotNull
    @Override
    public final String getTitle(Player player) {
        return getTitle(player, page) + "&r &8[&r" + page + "&8/&r" + getMaxPage(player) + "&8]";
    }

    public final int getMaxPage(Player player) {
        int itemSize = getItems(player).size();
        int itemsPerPage = getItemsPerPage(player);

        return (int) Math.ceil((double) itemSize / itemsPerPage);
    }

    @Override
    public final int getSize(Player player) {
        return (9 * 2) + getItemsPerPage(player);
    }

    @Override
    public final Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        List<Button> items = getItems(player);
        Comparator<? super Button> comparator = sortBy(player);
        if (comparator != null) {
            items.sort(comparator);
        }

        int itemsPerPage = getItemsPerPage(player);
        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, items.size());

        for (int i = start; i < end; i++) {
            Button item = items.get(i);
            buttons.put(i - start + 9, item);
        }

        return buttons;
    }

    @Deprecated
    @Override
    public InventoryType getInventoryType(Player player) {
        return null;
    }
}
