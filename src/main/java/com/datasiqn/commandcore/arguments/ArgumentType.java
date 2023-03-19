package com.datasiqn.commandcore.arguments;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.context.CommandContext;
import com.datasiqn.commandcore.util.ParseUtil;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents an argument type
 * @param <T> The type of the argument
 */
public interface ArgumentType<T> {
    ArgumentType<String> STRING = new StringArgumentType();

    ArgumentType<Integer> NATURAL_NUMBER = new CustomArgumentType<>(str -> Result.resolve(() -> Integer.parseInt(str), error -> "Invalid integer " + str).andThen(integer -> integer <= 0 ? Result.error("Integer must not be below 0") : Result.ok(integer)));

    ArgumentType<Integer> INTEGER = new CustomArgumentType<>(str -> Result.resolve(() -> Integer.parseInt(str), error -> "Invalid integer " + str));

    ArgumentType<Double> DOUBLE = new CustomArgumentType<>(str -> Result.resolve(() -> Double.parseDouble(str), error -> "Invalid double " + str));

    ArgumentType<Boolean> BOOLEAN = new CustomArgumentType<>(str -> Result.resolve(() -> ParseUtil.strictParseBoolean(str), error -> "Invalid boolean " + str + ", expected either true or false"), Arrays.asList("true", "false"));

    ArgumentType<java.util.UUID> UUID = new CustomArgumentType<>(str -> Result.resolve(() -> java.util.UUID.fromString(str), error -> "Invalid UUID " + str));

    ArgumentType<Material> MATERIAL = new EnumArgumentType<>(Material.class, "material");

    ArgumentType<Material> BLOCK = new CustomArgumentType<>(str -> Result.resolve(() -> EnumUtils.findEnumInsensitiveCase(Material.class, str), error -> "Invalid block " + str).andThen(material -> material.isBlock() ? Result.ok(material) : Result.error("Invalid block " + str)));

    ArgumentType<Player> PLAYER = new CustomArgumentType<>(name -> Result.ofNullable(Bukkit.getPlayerExact(name), "No player exists with the name " + name), context -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

    ArgumentType<Command> COMMAND = new CustomArgumentType<>(str -> Result.ofNullable(CommandCore.getInstance().getCommandManager().getCommand(str), "No command exists with the name " + str), context -> {
        List<String> commandNames = new ArrayList<>();
        CommandCore.getInstance().getCommandManager().allCommands().forEach((name, command) -> {
            if (context.getSource().hasPermission(command.getPermissionString())) commandNames.add(name);
        });
        return commandNames;
    });

    /**
     * Parses a string
     * @param str The string to parse
     * @return The result of parsing
     */
    @NotNull
    Result<T, String> parse(@NotNull String str);

    /**
     * Gets the tabcomplete for this {@code ArgumentType}
     * @param context The command context
     * @return The tabcomplete
     */
    @NotNull
    default List<String> getTabComplete(@NotNull CommandContext context) {
        return new ArrayList<>();
    }

    /**
     * Represents a custom argument type that parses to an enum value
     * @param <T> The type of the enum
     */
    class EnumArgumentType<T extends Enum<T>> extends CustomArgumentType<T> {
        /**
         * Creates a new {@code ArgumentType}
         * @param enumClass The enum's class
         */
        public EnumArgumentType(@NotNull Class<T> enumClass) {
            this(enumClass, enumClass.getSimpleName());
        }
        /**
         * Creates a new {@code ArgumentType}
         * @param enumClass The enum's class
         * @param enumName The name of the enum
         */
        public EnumArgumentType(@NotNull Class<T> enumClass, @NotNull String enumName) {
            super(str -> Result.resolve(() -> EnumUtils.findEnumInsensitiveCase(enumClass, str), error -> "Invalid " + enumName + " '" + str + "'"), Arrays.stream(enumClass.getEnumConstants()).map(t -> t.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList()));
        }
    }

    /**
     * Represents a custom argument type
     * @param <T> The type of the argument
     */
    class CustomArgumentType<T> implements ArgumentType<T> {
        private final @NotNull Function<String, Result<T, String>> parseFunction;
        private List<String> values;
        private Function<CommandContext, List<String>> valueFunction;

        /**
         * Creates a new {@link ArgumentType}
         * @param parseFunction The function to use when parsing a string
         */
        public CustomArgumentType(@NotNull Function<String, Result<T, String>> parseFunction) {
            this(parseFunction, Collections.emptyList());
        }
        /**
         * Creates a new {@link ArgumentType}
         * @param parseFunction The function to use when parsing a string
         * @param values The tabcomplete values
         */
        public CustomArgumentType(@NotNull Function<String, Result<T, String>> parseFunction, @NotNull List<String> values) {
            this.parseFunction = parseFunction;
            this.values = values;
        }
        /**
         * Creates a new {@link ArgumentType}
         * @param parseFunction The function to use when parsing a string
         * @param valueFunction A function of tabcomplete values
         */
        public CustomArgumentType(@NotNull Function<String, Result<T, String>> parseFunction, @NotNull Function<CommandContext, List<String>> valueFunction) {
            this.parseFunction = parseFunction;
            this.valueFunction = valueFunction;
        }

        @Override
        public @NotNull Result<T, String> parse(@NotNull String str) {
            return parseFunction.apply(str);
        }

        @Override
        public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
            return values == null ? valueFunction.apply(context) : values;
        }
    }

    /**
     * Represents a string argument type
     */
    class StringArgumentType implements ArgumentType<String> {
        @Override
        public @NotNull Result<String, String> parse(@NotNull String str) {
            return Result.ok(str);
        }
    }
}
