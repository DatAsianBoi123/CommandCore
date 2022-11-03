package com.datasiqn.commandcore.arguments;

import com.datasiqn.commandcore.ArgumentParseException;
import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Represents an argument type
 * @param <T> The type of the argument
 */
public interface ArgumentType<T> {
    ArgumentType<String> STRING = new StringArgumentType();

    ArgumentType<Integer> INTEGER = new CustomArgumentType<>(str -> {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException("Invalid integer " + str);
        }
    });

    ArgumentType<Integer> NATURAL_NUMBER = new CustomArgumentType<>(str -> {
        int integer;
        try {
            integer = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException("Invalid integer " + str);
        }
        if (integer <= 0) throw new ArgumentParseException("Integer must not be below 0");
        return integer;
    });

    ArgumentType<Boolean> BOOLEAN = new CustomArgumentType<>(str -> {
        if (str.equalsIgnoreCase("true")) return true;
        else if (str.equalsIgnoreCase("false")) return false;
        throw new ArgumentParseException("Invalid boolean " + str + ", expected either true or false");
    }, Arrays.asList("true", "false"));

    ArgumentType<Player> PLAYER = new CustomArgumentType<>(name -> {
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) throw new ArgumentParseException("No player exists with the name " + name);
        return player;
    }, () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

    ArgumentType<Command> COMMAND = new CustomArgumentType<>(str -> {
        Command executor = CommandCore.getInstance().getCommandManager().getCommand(str);
        if (executor == null) throw new ArgumentParseException("No command exists with the name " + str);
        return executor;
    }, () -> new ArrayList<>(CommandCore.getInstance().getCommandManager().allCommands().keySet()));

    /**
     * Parses a string
     * @param str The string to parse
     * @return The parsed string
     * @throws ArgumentParseException If an exception occurs when parsing
     */
    @NotNull
    T parse(@NotNull String str) throws ArgumentParseException;

    /**
     * Gets the tabcomplete for this {@code ArgumentType}
     * @return The tabcomplete
     */
    @NotNull
    default List<String> getTabComplete() {
        return new ArrayList<>();
    }

    /**
     * Represents a custom argument type that parses to an enum value
     * @param <T> The type of the enum
     */
    class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {
        private final Class<T> enumClass;

        /**
         * Creates a new {@code ArgumentType}
         * @param enumClass The enum's class
         */
        public EnumArgumentType(Class<T> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public @NotNull T parse(@NotNull String str) throws ArgumentParseException {
            try {
                return T.valueOf(enumClass, str.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ArgumentParseException("Invalid " + enumClass.getSimpleName() + " '" + str + "'");
            }
        }

        @Override
        public @NotNull List<String> getTabComplete() {
            return Arrays.stream(enumClass.getEnumConstants()).map(t -> t.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());
        }
    }

    /**
     * Represents a custom argument type
     * @param <T> The type of the argument
     */
    class CustomArgumentType<T> implements ArgumentType<T> {
        private final ParseFunction<T> asStringFunction;
        private List<String> values;
        private Supplier<List<String>> valueSupplier;

        /**
         * Creates a new {@link ArgumentType}
         * @param parseFunction The function to use when parsing a string
         */
        public CustomArgumentType(ParseFunction<T> parseFunction) {
            this(parseFunction, Collections.emptyList());
        }

        /**
         * Creates a new {@link ArgumentType}
         * @param asStringFunction The function to use when parsing a string
         * @param values The tabcomplete values
         */
        public CustomArgumentType(ParseFunction<T> asStringFunction, List<String> values) {
            this.asStringFunction = asStringFunction;
            this.values = values;
        }

        /**
         * Creates a new {@link ArgumentType}
         * @param parseFunction The function to use when parsing a string
         * @param valueSupplier A supplier of tabcomplete values
         */
        public CustomArgumentType(ParseFunction<T> parseFunction, Supplier<List<String>> valueSupplier) {
            this.asStringFunction = parseFunction;
            this.valueSupplier = valueSupplier;
        }

        @Override
        public @NotNull T parse(@NotNull String str) throws ArgumentParseException {
            return asStringFunction.apply(str);
        }

        @Override
        public @NotNull List<String> getTabComplete() {
            return values == null ? valueSupplier.get() : values;
        }

        /**
         * Represents the function used when parsing a string
         * @param <T> The type of the argument
         */
        @FunctionalInterface
        public interface ParseFunction<T> {
            /**
             * Parses a string
             * @param str The string
             * @return The parsed string
             * @throws ArgumentParseException If there's a parsing error
             */
            T apply(String str) throws ArgumentParseException;
        }
    }

    /**
     * Represents a string argument type
     */
    class StringArgumentType implements ArgumentType<String> {
        @Override
        public @NotNull String parse(@NotNull String str) {
            return str;
        }
    }
}
