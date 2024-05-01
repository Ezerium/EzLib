package com.ezerium.jda.command;

import com.google.common.base.Preconditions;
import lombok.Getter;

public class CommandContext {

    private final String[] args;

    public CommandContext(String[] args) {
        this.args = args;
    }

    public Argument read(int index) {
        if (index >= args.length)
            throw new IndexOutOfBoundsException();
        return new Argument(null, args[index]);
    }

    public static class Argument {

        @Getter
        private final String name;
        private final Object value;

        public Argument(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public Object asObject() {
            return value;
        }

        public String asString() {
            Preconditions.checkArgument(value instanceof String, "Value is not a String");
            return (String) value;
        }

        public int asInt() {
            Preconditions.checkArgument(value instanceof Integer, "Value is not an Integer");
            return (int) value;
        }

        public long asLong() {
            Preconditions.checkArgument(value instanceof Long, "Value is not a Long");
            return (long) value;
        }

        public double asDouble() {
            Preconditions.checkArgument(value instanceof Double, "Value is not a Double");
            return (double) value;
        }

        public float asFloat() {
            Preconditions.checkArgument(value instanceof Float, "Value is not a Float");
            return (float) value;
        }

        public boolean asBoolean() {
            Preconditions.checkArgument(value instanceof Boolean, "Value is not a Boolean");
            return (boolean) value;
        }

        public <T> T asType(Class<T> type) {
            Preconditions.checkArgument(type.isInstance(value), "Value is not of type " + type.getSimpleName());
            return type.cast(value);
        }

    }

}
