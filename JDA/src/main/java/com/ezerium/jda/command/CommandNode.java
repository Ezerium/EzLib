package com.ezerium.jda.command;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CommandNode {

    private final String name;
    private final String description;

    @Nullable
    private final ICommand command;

    private final Map<String, CommandNode> children;

    public CommandNode(String name, String description, @Nullable ICommand command, @Nullable Method method) {
        this.name = name;
        this.description = description;
        this.command = command;
        this.children = new HashMap<>();
    }

    public void execute() {

    }

}
