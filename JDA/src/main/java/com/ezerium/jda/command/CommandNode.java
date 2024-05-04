package com.ezerium.jda.command;

import com.ezerium.utils.ReflectionUtils;
import lombok.Data;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CommandNode {

    private final String name;
    private final String description;

    @NotNull
    private final ICommand command;

    private final Map<String, CommandNode> children;

    public CommandNode(String name, String description, @NotNull ICommand command) {
        this.name = name;
        this.description = description;
        this.command = command;
        this.children = new HashMap<>();
    }

    public void execute(SlashCommandInteractionEvent event) {
        SlashCommandInteraction commandData = event.getInteraction();
        Member member = commandData.getMember();
        if (member == null) return;

        List<OptionMapping> options = event.getOptions();

        Map<String, Object> optionsMap = new HashMap<>();
        for (OptionMapping option : options) {
            String name = option.getName();
            DataObject dataObject = (DataObject) ReflectionUtils.getFieldValue(option, "data");
            Map<String, Object> values = dataObject.toMap();

            optionsMap.put(name, values.get("value"));
        }

        CommandContext context = new CommandContext(member, optionsMap, commandData);
        command.execute(context);
    }

    public void execute(Member member, String[] args, MessageReceivedEvent e) {
        command.execute(new CommandContext(member, args, e.getMessage()));
    }

}
