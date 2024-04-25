package com.ezerium.spigot.gui.button;

import com.ezerium.spigot.utils.Util;
import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Button {

    @NotNull
    abstract public String getName(Player player);

    @NotNull
    abstract public List<String> getLore(Player player);

    @NotNull
    abstract public Material getMaterial(Player player);

    abstract public void click(Player player, ClickType clickType, int slot);

    public int getAmount(Player player) {
        return 1;
    }

    public byte getDamage(Player player) {
        return 0;
    }

    public boolean glow(Player player) {
        return false;
    }

    @NotNull
    public Map<Enchantment, Integer> getEnchantments(Player player) {
        return new HashMap<>();
    }

    public boolean cancelClick(Player player) {
        return true;
    }

    public boolean hideAttributes(Player player) {
        return false;
    }

    public boolean hideEnchantments(Player player) {
        return false;
    }

    public boolean hidePotionEffects(Player player) {
        return false;
    }

    public boolean hidePlacedOn(Player player) {
        return false;
    }

    public boolean hideDestroys(Player player) {
        return false;
    }

    public boolean hideUnbreakable(Player player) {
        return false;
    }

    public final ItemStack build(Player player) {
        Preconditions.checkNotNull(player, "Player cannot be null.");
        Preconditions.checkNotNull(getName(player), "Name cannot be null.");
        Preconditions.checkNotNull(getLore(player), "Lore cannot be null.");
        Preconditions.checkNotNull(getMaterial(player), "Material cannot be null.");

        ItemStack item = new ItemStack(getMaterial(player), getAmount(player), getDamage(player));
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(Util.format(getName(player)));
        meta.setLore(Util.format(getLore(player)));

        Map<Enchantment, Integer> enchantments = getEnchantments(player);
        Preconditions.checkNotNull(enchantments, "Enchantments cannot be null.");
        enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
        if (glow(player)) {
            Preconditions.checkArgument(enchantments.isEmpty(), "Cannot have enchantments and glow at the same time.");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (hideAttributes(player)) meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if (hideEnchantments(player)) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if (hidePotionEffects(player)) meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        if (hidePlacedOn(player)) meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        if (hideDestroys(player)) meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        if (hideUnbreakable(player)) meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);
        return item;
    }

}
