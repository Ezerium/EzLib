package com.ezerium.jda.command;

public interface ICommand {

    String getName();

    String getDescription();

    void execute(CommandContext context);

    enum CommandType {
        SLASH_COMMAND,
        MESSAGE_COMMAND
    }

}
