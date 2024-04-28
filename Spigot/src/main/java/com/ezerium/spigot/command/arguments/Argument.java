package com.ezerium.spigot.command.arguments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
@ToString
public class Argument {

    private final ArgumentType type;
    private final String name;
    @Nullable
    private final Object value;

    private final String flagName;
    private final boolean a;

    public Argument(ArgumentType type, String name, @Nullable Object value, String flagName) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.flagName = flagName;
        this.a = false;
    }

}
