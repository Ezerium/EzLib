package com.ezerium.spigot.command.arguments;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
public class Argument {

    private final ArgumentType type;
    private final String name;
    @Nullable
    private final Object value;

    private final String flagName;

}
