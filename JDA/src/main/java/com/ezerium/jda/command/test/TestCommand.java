package com.ezerium.jda.command.test;

import com.ezerium.jda.command.CommandContext;
import com.ezerium.jda.command.ICommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class TestCommand implements ICommand {

    @Override
    public void execute(CommandContext context) {
        CommandContext.Argument test = context.read("test");
        CommandContext.Argument test2 = context.read("test2");

        CommandContext.Argument test1Index = context.read(0);
        CommandContext.Argument test2Index = context.read(1);

        context.reply(
            "test: " + test.asString() + "\n" +
            "test2: " + (test2.isNull() ? "null" : test2.asDouble()) + "\n" +
            "test1Index: " + test1Index.asString() + "\n" +
            "test2Index: " + (test2Index.isNull() ? "null" : test2Index.asDouble())
        );
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getDescription() {
        return "test";
    }

    @Override
    public Option[] getOptions() {
        return new Option[] {
            new Option("test", "test", true),
            new Option("test2", "test2", false, OptionType.NUMBER)
        };
    }
}
