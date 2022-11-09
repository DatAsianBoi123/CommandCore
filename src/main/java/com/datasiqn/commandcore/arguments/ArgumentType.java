package com.datasiqn.commandcore.arguments;

import com.datasiqn.commandcore.ArgumentParseException;
import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.result.Result;
import com.datasiqn.commandcore.util.ParseUtil;
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

    ArgumentType<Integer> INTEGER = new CustomArgumentType<>(str -> Result.resolve(() -> Integer.parseInt(str)).mapError(error -> new ArgumentParseException("Invalid integer " + str)));

    ArgumentType<Integer> NATURAL_NUMBER = new CustomArgumentType<>(str -> Result.resolve(() -> Integer.parseInt(str)).mapError(error -> new ArgumentParseException("Invalid integer " + str)).and(integer -> {
        if (integer <= 0) return Result.error(new ArgumentParseException("Integer must not be below 0"));
        return Result.ok(integer);
    }));

    ArgumentType<Boolean> BOOLEAN = new CustomArgumentType<>(str -> Result.resolve(() -> ParseUtil.strictParseBoolean(str)).mapError(error -> new ArgumentParseException("Invalid boolean " + str + ", expected either true or false")), Arrays.asList("true", "false"));

    ArgumentType<Player> PLAYER = new CustomArgumentType<>(name -> Result.ofNullable(Bukkit.getPlayerExact(name), new ArgumentParseException("No player exists with the name " + name)), () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

    ArgumentType<Command> COMMAND = new CustomArgumentType<>(str -> Result.ofNullable(CommandCore.getInstance().getCommandManager().getCommand(str), new ArgumentParseException("No command exists with the name " + str)), () -> new ArrayList<>(CommandCore.getInstance().getCommandManager().allCommands().keySet()));

    /**
     * Parses a string
     * @param str The string to parse
     * @return The result of parsing
     */
    @NotNull
    Result<T, ArgumentParseException> parse(@NotNull String str);

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
        public @NotNull Result<T, ArgumentParseException> parse(@NotNull String str) {
            try {
                return Result.ok(T.valueOf(enumClass, str.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return Result.error(new ArgumentParseException("Invalid " + enumClass.getSimpleName() + " '" + str + "'"));
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
        private final ParseFunction<T> parseFunction;
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
         * @param parseFunction The function to use when parsing a string
         * @param values The tabcomplete values
         */
        public CustomArgumentType(ParseFunction<T> parseFunction, List<String> values) {
            this.parseFunction = parseFunction;
            this.values = values;
        }

        /**
         * Creates a new {@link ArgumentType}
         * @param parseFunction The function to use when parsing a string
         * @param valueSupplier A supplier of tabcomplete values
         */
        public CustomArgumentType(ParseFunction<T> parseFunction, Supplier<List<String>> valueSupplier) {
            this.parseFunction = parseFunction;
            this.valueSupplier = valueSupplier;
        }

        @Override
        public @NotNull Result<T, ArgumentParseException> parse(@NotNull String str) {
            return parseFunction.parse(str);
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
             * @return The result of the parsing
             */
            Result<T, ArgumentParseException> parse(String str);
        }
    }

    /**
     * Represents a string argument type
     */
    class StringArgumentType implements ArgumentType<String> {
        @Override
        public @NotNull Result<String, ArgumentParseException> parse(@NotNull String str) {
            return Result.ok(str);
        }
    }
}
