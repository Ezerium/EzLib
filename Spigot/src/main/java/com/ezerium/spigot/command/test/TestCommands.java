package com.ezerium.spigot.command.test;

import com.ezerium.annotations.command.*;
import com.ezerium.spigot.chat.Pagination;
import com.ezerium.spigot.disguise.Disguise;
import com.ezerium.spigot.disguise.DisguiseHandler;
import com.ezerium.spigot.gui.test.TestMenu;
import com.ezerium.spigot.utils.PlayerUtils;
import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestCommands {

    @Command("test")
    public void test(CommandSender sender) {
        sender.sendMessage("Test command executed.");
    }

    @Command("test2")
    @Aliases("t2")
    public void test2(CommandSender sender, @Arg("arg") String arg) {
        sender.sendMessage("Test2 command executed.");
        sender.sendMessage("Arg: " + arg);
    }

    @Command("test3")
    public void test3(CommandSender sender, @Arg("arg") String arg, @Arg("arg2") String arg2) {
        sender.sendMessage("Test3 command executed.");
        sender.sendMessage("Arg: " + arg);
        sender.sendMessage("Arg2: " + arg2);
    }

    @Command("test4")
    public void test4(CommandSender sender, @Flag("f") boolean f, @Arg("arg2") String arg2) {
        sender.sendMessage("Test4 command executed.");
        sender.sendMessage("Flag: " + f);
        sender.sendMessage("Arg2: " + arg2);
    }

    @Command("test5")
    public void test5(CommandSender sender, @FlagValue(flagName = "f", argName = "value") String f) {
        sender.sendMessage("Test5 command executed.");
        sender.sendMessage("Flag: " + f);
    }

    @Command("test6")
    public void test6(CommandSender sender, @FlagValue(flagName = "f", argName = "value") String f, @Arg("arg") String arg) {
        sender.sendMessage("Test6 command executed.");
        sender.sendMessage("Flag: " + f);
        sender.sendMessage("Arg: " + arg);
    }

    @Command("test sub")
    public void testSub(CommandSender sender) {
        sender.sendMessage("Test sub command executed.");
    }

    @Command("test sub2")
    public void testSub2(CommandSender sender, @Arg("arg") String arg) {
        sender.sendMessage("Test sub2 command executed.");
        sender.sendMessage("Arg: " + arg);
    }

    private static DisguiseHandler disguiseHandler;

    @Command("disguisetest")
    @Description("Disguise test command.")
    public void disguiseTest(Player player, @Arg("player") String target) {
        if (disguiseHandler == null) {
            disguiseHandler = new DisguiseHandler();
        }

        Disguise disguise = new Disguise(PlayerUtils.getUUID(target), target, player.getUniqueId(), player.getName());
        disguiseHandler.disguise(disguise);
    }

    @Command("pagination")
    public void pagination(CommandSender sender, @Arg(value = "page", defaultValue = "1") int page) {
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 32, 2, 8, 4, 8);
        new Pagination<Integer>() {

            @Nullable
            @Override
            public String getHeader(int page, int maxPage) {
                return "&7---------------";
            }

            @Override
            public String format(Integer num, int page, int maxPage) {
                return "Number: " + num;
            }

        }.send(sender, page, list);
    }

    @Command("gui")
    public void gui(Player player) {
        new TestMenu().open(player);
    }

}
