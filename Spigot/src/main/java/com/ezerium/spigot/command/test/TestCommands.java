package com.ezerium.spigot.command.test;

import com.ezerium.annotations.command.Aliases;
import com.ezerium.annotations.command.Arg;
import com.ezerium.annotations.command.Command;
import com.ezerium.annotations.command.Flag;
import org.bukkit.command.CommandSender;

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

}
