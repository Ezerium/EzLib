package com.ezerium.jda.command;

import com.ezerium.annotations.command.Arg;
import com.google.common.base.Preconditions;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CommandContext {

    @Getter
    private final Member executor;
    @Getter
    private final MessageChannelUnion channel;
    @Nullable
    private final Message message;
    @Nullable
    private final SlashCommandInteraction interaction;

    @Getter
    @Nullable
    private String[] args;
    private final Map<String, Object> options;

    public CommandContext(Member member, @NotNull String[] args, Message message) {
        this.executor = member;
        this.args = args;
        this.options = null;
        this.message = message;
        this.interaction = null;
        this.channel = message.getChannel();
    }

    public CommandContext(Member member, Map<String, Object> options, SlashCommandInteraction interaction) {
        this.executor = member;
        this.args = null;
        this.options = options;
        this.message = null;
        this.interaction = interaction;
        this.channel = interaction.getChannel();
    }

    public void reply(@NotNull String message) {
        this.reply(message, null, false);
    }

    public void reply(@NotNull EmbedBuilder builder) {
        this.reply(null, builder, false);
    }

    public void reply(@NotNull String message, boolean ephemeral) {
        this.reply(message, null, ephemeral);
    }

    public void reply(@NotNull EmbedBuilder builder, boolean ephemeral) {
        this.reply(null, builder, ephemeral);
    }

    public void reply(String content, EmbedBuilder embed, boolean ephemeral) {
        Preconditions.checkArgument(content != null || embed != null, "Content and embed cannot be null");
        if (message != null) {
            if (content != null && embed != null) {
                message.reply(content).setEmbeds(embed.build()).queue();
            } else if (content != null) {
                message.reply(content).queue();
            } else {
                message.replyEmbeds(embed.build()).queue();
            }
        } else if (interaction != null) {
            if (content != null && embed != null) {
                interaction.deferReply(ephemeral).setContent(content).addEmbeds(embed.build()).queue();
            } else if (content != null) {
                interaction.deferReply(ephemeral).setContent(content).queue();
            } else {
                interaction.deferReply(ephemeral).addEmbeds(embed.build()).queue();
            }
        } else {
            throw new IllegalStateException("Cannot reply to null message or interaction");
        }
    }

    public int size() {
        return args == null ? options.size() : args.length;
    }

    public Argument next() {
        Argument argument;
        if (args != null) {
            if (args.length == 0) return null;
            argument = read(0);
            System.arraycopy(args, 1, args, 0, args.length - 1);
        } else {
            if (options.isEmpty()) return null;
            argument = read(0);
            options.remove(argument.getName());
        }

        return argument;
    }

    public boolean hasNext() {
        return args != null ? args.length > 0 : !options.isEmpty();
    }

    public Argument read(int index) {
        Preconditions.checkArgument(index >= 0, "Index cannot be negative");
        if (args == null) {
            if (index >= options.size())
                return new Argument(null, null);
            String name = null;
            for (int i = options.size(); i >= 0; i--) {
                if (i == options.size() - index) {
                    name = (String) options.keySet().toArray()[i - 1];
                    break;
                }
            }
            return new Argument(name, options.getOrDefault(name, null));
        } else {
            if (index >= args.length)
                return new Argument(null, null);
            return new Argument(null, args[index]);
        }
    }

    public Argument read(@NotNull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        Preconditions.checkState(options != null, "Cannot read options from arguments");
        return new Argument(name, options.getOrDefault(name, null));
    }

    public static class Argument {

        @Getter
        private final String name;
        private final Object value;

        public Argument(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public boolean isNull() {
            return value == null;
        }

        public Object asObject() {
            return value;
        }

        public String asString() {
            Preconditions.checkArgument(value instanceof String, "Value `" + value + "` is not a String in '" + name + "'");
            return (String) value;
        }

        public int asInt() {
            Preconditions.checkArgument(value instanceof Integer, "Value `" + value + "` is not an Integer in '" + name + "'");
            return (int) value;
        }

        public long asLong() {
            Preconditions.checkArgument(value instanceof Long, "Value `" + value + "` is not a Long in '" + name + "'");
            return (long) value;
        }

        public double asDouble() {
            Preconditions.checkArgument(value instanceof Double, "Value `" + value + "` is not a Double in '" + name + "'");
            return (double) value;
        }

        public float asFloat() {
            Preconditions.checkArgument(value instanceof Float, "Value `" + value + "` is not a Float in '" + name + "'");
            return (float) value;
        }

        public boolean asBoolean() {
            Preconditions.checkArgument(value instanceof Boolean, "Value `" + value + "` is not a Boolean in '" + name + "'");
            return (boolean) value;
        }

        public <T> T asType(Class<T> type) {
            Preconditions.checkArgument(type.isInstance(value), "Value `" + value + "` is not of type " + type.getSimpleName() + " in '" + name + "'");
            return type.cast(value);
        }

    }

}
