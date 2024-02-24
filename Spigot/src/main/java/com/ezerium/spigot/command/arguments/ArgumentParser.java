package com.ezerium.spigot.command.arguments;

import com.ezerium.shared.annotations.command.Arg;
import com.ezerium.shared.annotations.command.Flag;
import com.ezerium.shared.annotations.command.FlagValue;
import com.ezerium.spigot.command.CommandHandler;
import com.ezerium.spigot.command.parameters.ParameterType;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.*;

public class ArgumentParser {

    private final Map<Integer, Argument> args;
    private final CommandSender executor;
    private final CommandHandler handler;

    public ArgumentParser(CommandSender executor, CommandHandler handler) {
        this.args = new HashMap<>();
        this.executor = executor;
        this.handler = handler;
    }

    public Map<Integer, Argument> parse(String[] args, Parameter[] parameters) {

        int i = 0;
        for (Parameter param : parameters) {
            i++;
            if (args.length == 0 && i != parameters.length && (param.isAnnotationPresent(Arg.class)
                    && param.getDeclaredAnnotation(Arg.class).defaultValue().isEmpty()))
                continue;

            if (param.isAnnotationPresent(Arg.class)) args = this.parseArgs(param, args);
            else if (param.isAnnotationPresent(Flag.class)) args = this.parseFlags(param, args);
            else if (param.isAnnotationPresent(FlagValue.class)) args = this.parseFlagValues(param, args);
            else {
                throw new RuntimeException("Cannot find any annotation for '" + param.getName() + "'. Please use @Arg, @Flag or @FlagValue.");
            }
        }

        if (args.length > 0) {
            this.args.put(999, new Argument(null, "unknown", args[0], null));
        }

        return this.args;
    }

    private String[] parseArgs(Parameter parameter, String[] args) {
        Arg arg = parameter.getAnnotation(Arg.class);
        if (arg.wildcard()) {
            if (args.length == 0) return new String[0];
            String value = String.join(" ", args);

            Argument argument = new Argument(ArgumentType.ARG, arg.value(), value, null);

            int size = this.args.size();
            this.args.put(size, argument);

            return new String[0];
        }

        String value;
        if (args.length == 0 && !arg.defaultValue().isEmpty()) value = arg.defaultValue();
        else if (args.length > 0) value = args[0];
        else return new String[0];

        if (value.startsWith("\"")) {
            if (value.endsWith("\"")) value = value.substring(1).substring(0, value.length() - 2);
            else {
                String newValue = value.substring(1);
                args = shift(args);
                String[] argsCopy = args.clone();
                for (String argument : args) {
                    newValue += " " + argument;
                    argsCopy = shift(argsCopy);
                    if (argument.endsWith("\"")) {
                        Argument a = new Argument(ArgumentType.ARG, arg.value(), newValue.substring(0, newValue.length() - 1), null);

                        int size = this.args.size();
                        this.args.put(size, a);

                        return argsCopy;
                    }
                }
            }
        }

        ParameterType<?> parameterType = handler.getParameterTypes().get(parameter.getType());
        if (parameterType == null) throw new RuntimeException();

        Object valueObject = parameterType.parse(executor, value);
        Argument argument = new Argument(ArgumentType.ARG, arg.value(), valueObject, null);

        int size = this.args.size();
        this.args.put(size, argument);

        return shift(args);
    }

    private String[] parseFlags(Parameter parameter, String[] args) {
        if (!parameter.getType().toString().equalsIgnoreCase("boolean")) throw new RuntimeException("@Flag only supports booleans.");

        Flag flag = parameter.getAnnotation(Flag.class);
        String flagName = "-" + flag.value();

        boolean hasFlag = Arrays.asList(args).contains(flagName);

        Argument argument = new Argument(ArgumentType.FLAG, null, hasFlag, flag.value());
        int size = this.args.size();
        this.args.put(size, argument);
        return (hasFlag ? shift(args, flagName) : args);
    }

    private String[] parseFlagValues(Parameter parameter, String[] args) {
        FlagValue flagValue = parameter.getAnnotation(FlagValue.class);
        String flagName = "-" + flagValue.flagName();

        if (args.length == 0) {
            Argument argument = new Argument(ArgumentType.FLAG_VALUE, flagValue.argName(), flagValue.defaultValue().isEmpty() ? null : flagValue.defaultValue(), flagValue.flagName());
            int size = this.args.size();
            this.args.put(size, argument);

            return args;
        }

        int index = -1;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals(flagName)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            args = shift(args, flagName);
            if (args.length == 0) return new String[0];

            String value = args[index];
            if (value.startsWith("\"")) {
                if (value.endsWith("\"")) value = value.substring(1).substring(0, value.length() - 2);
                else {
                    String newValue = value.substring(1);
                    args = shift(args);
                    String[] argsCopy = args.clone();
                    for (String argument : args) {
                        newValue += " " + argument;
                        argsCopy = shift(argsCopy);
                        if (argument.endsWith("\"")) {
                            Argument a = new Argument(ArgumentType.FLAG_VALUE, flagValue.argName(), newValue.substring(0, newValue.length() - 1), flagName);

                            int size = this.args.size();
                            this.args.put(size, a);

                            return argsCopy;
                        }
                    }
                }
            }

            ParameterType<?> parameterType = handler.getParameterTypes().get(parameter.getType());
            if (parameterType == null) throw new RuntimeException();

            Object valueObject = parameterType.parse(executor, value);
            Argument argument = new Argument(ArgumentType.FLAG_VALUE, flagValue.argName(), valueObject, flagValue.flagName());

            int size = this.args.size();
            this.args.put(size, argument);
            return shift(args, args[index]);
        }

        Argument argument = new Argument(ArgumentType.FLAG_VALUE, flagValue.argName(), null, flagValue.flagName());
        int size = this.args.size();
        this.args.put(size, argument);

        return args;
    }

    private String[] shift(String[] array) {
        String[] newArray = new String[array.length - 1];
        System.arraycopy(array, 1, newArray, 0, newArray.length);
        return newArray;
    }

    private String[] shift(String[] array, String value) {
        List<String> list = new ArrayList<>(Arrays.asList(array));
        list.remove(value);
        return list.toArray(new String[0]);
    }

}
