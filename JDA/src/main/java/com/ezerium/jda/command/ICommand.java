package com.ezerium.jda.command;

import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.Nullable;

public interface ICommand {

    String getName();

    String getDescription();

    void execute(CommandContext context);

    default Option[] getOptions() {
        return new Option[0];
    }

    default CommandType getType() {
        return CommandType.SLASH_COMMAND;
    }

    @Nullable
    default String subOf() {
        return null;
    }

    enum CommandType {
        SLASH_COMMAND,
        MESSAGE_COMMAND
    }

    @Getter
    @ToString
    class Option {
        private final String name;
        private final String description;
        private final boolean required;
        private final net.dv8tion.jda.api.interactions.commands.OptionType type;

        public Option(String name, String description, boolean required, net.dv8tion.jda.api.interactions.commands.OptionType type) {
            this.name = name;
            this.description = description;
            this.required = required;
            this.type = type;
        }

        public Option(String name, String description, net.dv8tion.jda.api.interactions.commands.OptionType type) {
            this(name, description, false, type);
        }

        public Option(String name, String description, boolean required) {
            this(name, description, required, net.dv8tion.jda.api.interactions.commands.OptionType.STRING);
        }

        public Option(String name, String description) {
            this(name, description, false, net.dv8tion.jda.api.interactions.commands.OptionType.STRING);
        }

    }

}
