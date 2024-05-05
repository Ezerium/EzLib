package com.ezerium.spigot.gui;

import com.ezerium.spigot.gui.button.Button;
import com.ezerium.spigot.gui.button.impl.GlassButton;
import com.ezerium.spigot.gui.button.impl.NextButton;
import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class PaginatedMenu extends Menu {

    public enum Position {
        TOP,
        BOTTOM
    }

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

    /**
     * The position of the next and previous page button.
     * @return The position, either top or bottom.
     */
    public Position getButtonPosition(Player player) {
        return Position.BOTTOM;
    }

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
        if (itemSize == 0) {
            return 1;
        }

        return (int) Math.ceil((double) itemSize / itemsPerPage);
    }

    @Override
    public final int getSize(Player player) {
        return (9 * 2) + getItemsPerPage(player);
    }

    @Override
    public final Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            buttons.put(i, new GlassButton());
        }

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

        int size = getSize(player);
        Position position = getButtonPosition(player);
        if (page > 1) {
            buttons.put((position == Position.BOTTOM ? size - 9 - 1 : 0), new NextButton(this));
        }

        if (((float) items.size() / itemsPerPage) > 1.0F) {
            buttons.put((position == Position.BOTTOM ? size - 1 : 9), new NextButton(this));
        }

        return buttons;
    }

    @Deprecated
    @Override
    public final InventoryType getInventoryType(Player player) {
        return null;
    }
}
