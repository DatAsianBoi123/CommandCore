package com.datasiqn.commandcore.argument;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.context.CommandContext;
import com.datasiqn.commandcore.util.ParseUtil;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents an argument type
 * @param <T> The type of the argument
 */
public interface ArgumentType<T> {
    /**
     * {@code ArgumentType} that is just a single word
     */
    ArgumentType<String> WORD = new CustomArgumentType<>(reader -> Result.ok(reader.nextWord()));

    /**
     * {@code ArgumentType} that represents the name of something. Can have multiple spaces in the name
     */
    ArgumentType<String> NAME = new CustomArgumentType<>(reader -> {
        StringBuilder builder = new StringBuilder();
        builder.append(reader.get());
        while (!reader.atEnd()) {
            builder.append(reader.next());
        }
        return Result.ok(builder.toString());
    });

    /**
     * {@code ArgumentType} that represents an integer
     */
    ArgumentType<Integer> INTEGER = new CustomArgumentType<>(reader -> Result.<String, String>ok(reader.nextWord())
            .andThen(word -> Result.resolve(() -> Integer.parseInt(word), error -> "Invalid integer " + word)));

    /**
     * {@code ArgumentType} that represents an integer that is no smaller than 1
     */
    ArgumentType<Integer> NATURAL_NUMBER = new CustomArgumentType<>(reader -> INTEGER.parse(reader)
            .andThen(integer -> integer <= 0 ? Result.error("Integer must not be below 0") : Result.ok(integer)));

    /**
     * {@code ArgumentType} that represents a double
     */
    ArgumentType<Double> DOUBLE = new CustomArgumentType<>(reader -> Result.<String, String>ok(reader.nextWord())
            .andThen(word -> Result.resolve(() -> Double.parseDouble(word), error -> "Invalid double " + word)));

    /**
     * {@code ArgumentType} that represents a boolean
     */
    ArgumentType<Boolean> BOOLEAN = new CustomArgumentType<>(reader -> Result.<String, String>ok(reader.nextWord())
            .andThen(word -> Result.resolve(() -> ParseUtil.strictParseBoolean(word), error -> "Invalid boolean " + word + ", expected either true or false")), Arrays.asList("true", "false"));

    /**
     * {@code ArgumentType} that represents a uuid
     */
    ArgumentType<java.util.UUID> UUID = new CustomArgumentType<>(reader -> Result.<String, String>ok(reader.nextWord())
            .andThen(word -> Result.resolve(() -> java.util.UUID.fromString(word), error -> "Invalid UUID " + word)));

    /**
     * {@code ArgumentType} that represents a vector
     */
    ArgumentType<Vector> VECTOR = new CustomArgumentType<>(reader -> {
        Result<Integer, String> x = INTEGER.parse(reader);
        if (x.isError()) return Result.error(x.unwrapError());
        if (reader.atEnd()) return Result.error("Expected 3 integers for a location, but got 1 instead");
        reader.next();

        Result<Integer, String> y = INTEGER.parse(reader);
        if (y.isError()) return Result.error(y.unwrapError());
        if (reader.atEnd()) return Result.error("Expected 3 integers for a location, but got 2 instead");
        reader.next();

        Result<Integer, String> z = INTEGER.parse(reader);
        if (z.isError()) return Result.error(z.unwrapError());
        if (!reader.atEnd()) reader.next();

        return Result.ok(new Vector(x.unwrap(), y.unwrap(), z.unwrap()));
    }, context -> {
        Result<Player, String> player = context.getSource().getPlayer();
        if (player.isError()) return Collections.emptyList();
        Block targetBlock = player.unwrap().getTargetBlockExact(5);
        if (targetBlock == null) return Collections.emptyList();
        Vector vector = targetBlock.getLocation().toVector();
        return Collections.singletonList(vector.getBlockX() + " " + vector.getBlockY() + " " + vector.getBlockZ());
    });

    /**
     * {@code ArgumentType} that represents a material
     */
    ArgumentType<Material> MATERIAL = new EnumArgumentType<>(Material.class, "material");

    /**
     * {@code ArgumentType} that represents a material where {@link Material#isBlock()} is {@code true}
     */
    ArgumentType<Material> BLOCK = new FilteredEnumArgumentType<>(Material.class, Material::isBlock, str -> "Invalid block '" + str + "'");

    /**
     * {@code ArgumentType} that represents a material where {@link Material#isItem()} is {@code true}
     */
    ArgumentType<Material> ITEM = new FilteredEnumArgumentType<>(Material.class, Material::isItem, str -> "Invalid item '" + str + "'");

    /**
     * {@code ArgumentType} that represents a player
     */
    ArgumentType<Player> PLAYER = new CustomArgumentType<>(reader -> Result.<String, String>ok(reader.nextWord())
            .andThen(word -> Result.ofNullable(Bukkit.getPlayerExact(word), "No player exists with the name " + word)), context -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

    /**
     * {@code ArgumentType} that represents a {@code CommandCore} command
     */
    ArgumentType<Command> COMMAND = new CustomArgumentType<>(reader -> Result.<String, String>ok(reader.nextWord())
            .andThen(word -> Result.ofNullable(CommandCore.getInstance().getCommandManager().getCommand(word), "No command exists with the name " + word)), context -> {
        List<String> commandNames = new ArrayList<>();
        CommandCore.getInstance().getCommandManager().allCommands().forEach((name, command) -> {
            if (context.getSource().hasPermission(command.getPermissionString())) commandNames.add(name);
        });
        return commandNames;
    });

    /**
     * Creates an {@code ArgumentType} that represents an integer with a minimum value (inclusive)
     * @param min The inclusive minimum value
     * @return The newly created {@code ArgumentType}
     */
    @Contract("_ -> new")
    static @NotNull ArgumentType<Integer> rangedInt(int min) {
        return new CustomArgumentType<>(reader -> Result.<String, String>ok(reader.nextWord())
                .andThen(word -> Result.resolve(() -> Integer.valueOf(word), error -> "Invalid integer " + word))
                .andThen(integer -> integer < min ? Result.error("Integer must be not be below " + min) : Result.ok(integer)));
    }
    /**
     * Creates an {@code ArgumentType} that represents an integer with a minimum (inclusive) and maximum (inclusive) value
     * @param min The inclusive minimum value
     * @param max The inclusive maximum value
     * @return
     */
    @Contract("_, _ -> new")
    static @NotNull ArgumentType<Integer> rangedInt(int min, int max) {
        return new CustomArgumentType<>(reader -> Result.<String, String>ok(reader.nextWord())
                .andThen(word -> Result.resolve(() -> Integer.valueOf(word), error -> "Invalid integer " + word))
                .andThen(integer -> integer < min || integer > max ? Result.error("Integer must be between " + min + " and " + max) : Result.ok(integer)));
    }

    /**
     * Parses a string
     * @param reader The reader to parse
     * @return The result of parsing
     */
    @NotNull
    Result<T, String> parse(@NotNull ArgumentReader reader);

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
     * Represents a custom {@code ArgumentType} that parses to an enum value
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
            super(reader -> Result.<String, String>ok(reader.nextWord())
                    .andThen(word -> Result.resolve(() -> EnumUtils.findEnumInsensitiveCase(enumClass, word), error -> "Invalid " + enumName + " '" + word + "'")), Arrays.stream(enumClass.getEnumConstants()).map(t -> t.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList()));
        }
    }

    /**
     * Represents a custom {@code ArgumentType} that parses to a filtered enum
     * @param <T> The type of the enum
     */
    class FilteredEnumArgumentType<T extends Enum<T>> extends CustomArgumentType<T> {
        /**
         * Creates a new {@code ArgumentType}
         * @param enumClass The enum's class
         * @param filter The filter that enum values must pass through
         * @param errorMessage A custom error message function to use when the argument is an invalid enum
         */
        public FilteredEnumArgumentType(@NotNull Class<T> enumClass, Predicate<T> filter, Function<String, String> errorMessage) {
            super(reader -> Result.<String, String>ok(reader.nextWord())
                            .andThen(word -> Result.resolve(() -> EnumUtils.findEnumInsensitiveCase(enumClass, word), error -> errorMessage.apply(word)).andThen(enumType -> filter.test(enumType) ? Result.ok(enumType) : Result.error(errorMessage.apply(word)))),
                    Arrays.stream(enumClass.getEnumConstants()).filter(filter).map(enumType -> enumType.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList()));
        }
    }

    /**
     * Represents a custom argument type
     * @param <T> The type of the argument
     */
    class CustomArgumentType<T> implements ArgumentType<T> {
        protected final @NotNull Function<ArgumentReader, Result<T, String>> parseFunction;
        protected List<String> values;
        protected Function<CommandContext, List<String>> valueFunction;

        /**
         * Creates a new {@link ArgumentType}
         * @param parseFunction The function to use when parsing a string
         */
        public CustomArgumentType(@NotNull Function<ArgumentReader, Result<T, String>> parseFunction) {
            this(parseFunction, Collections.emptyList());
        }
        /**
         * Creates a new {@link ArgumentType}
         * @param parseFunction The function to use when parsing a string
         * @param values The tabcomplete values
         */
        public CustomArgumentType(@NotNull Function<ArgumentReader, Result<T, String>> parseFunction, @NotNull List<String> values) {
            this.parseFunction = parseFunction;
            this.values = values;
        }
        /**
         * Creates a new {@link ArgumentType}
         * @param parseFunction The function to use when parsing a string
         * @param valueFunction A function of tabcomplete values
         */
        public CustomArgumentType(@NotNull Function<ArgumentReader, Result<T, String>> parseFunction, @NotNull Function<CommandContext, List<String>> valueFunction) {
            this.parseFunction = parseFunction;
            this.valueFunction = valueFunction;
        }

        @Override
        public @NotNull Result<T, String> parse(@NotNull ArgumentReader reader) {
            return parseFunction.apply(reader);
        }

        @Override
        public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
            return values == null ? valueFunction.apply(context) : values;
        }
    }
}
