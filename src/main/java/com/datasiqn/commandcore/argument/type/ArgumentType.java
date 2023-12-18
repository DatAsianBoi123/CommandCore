package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.util.EnumUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
    ArgumentType<String> WORD = new WordArgumentType();

    /**
     * {@code ArgumentType} that represents a word that is enclosed in quotes.
     * <br>
     * Examples of this include:
     * <pre>
     *     "name with multiple spaces" -> name with multiple spaces
     *     "hey look, this one has \"quotes\" too!" -> hey look, this one has "quotes" too!
     *     "i can also escape backslashes as well \\" -> i can also escape backslashes as well \
     * </pre>
     * If you don't have any other nodes connected to this one, you can use {@link #NAME} instead.
     */
    ArgumentType<String> QUOTED_WORD = new QuotedWordArgumentType();

    /**
     * {@code ArgumentType} that represents the name of something.
     * Can have multiple spaces in the name, and does not need quotes enclosing them.
     * <br>
     * Examples of this include:
     * <pre>
     *     cool name
     *     oneword
     *     a lot of words
     *     includes "special characters" (and numbers) and #symbols#
     * </pre>
     * <strong>WARNING: Using this {@code ArgumentType} will make it impossible to execute any other nodes off of it.</strong>
     */
    ArgumentType<String> NAME = new NameArgumentType();

    /**
     * {@code ArgumentType} that represents a boolean
     */
    ArgumentType<Boolean> BOOLEAN = new BoolArgumentType();

    /**
     * {@code ArgumentType} that represents a UUID
     */
    ArgumentType<java.util.UUID> UUID = new UuidArgumentType();

    /**
     * {@code ArgumentType} that represents a vector
     */
    ArgumentType<Vector> VECTOR = new VectorArgumentType();

    /**
     * {@code ArgumentType} that represents a loaded world
     */
    ArgumentType<World> WORLD = new WorldArgumentType();

    /**
     * {@code ArgumentType} that represents an entity
     */
    ArgumentType<EntityType> ENTITY = new EnumArgumentType<>(EntityType.class, "entity");

    /**
     * {@code ArgumentType} that represents an entity that is living
     */
    ArgumentType<EntityType> LIVING_ENTITY = new FilteredEnumArgumentType<>(EntityType.class, EntityType::isAlive, "living entity");

    /**
     * {@code ArgumentType} that represents an entity that can be spawned using {@link org.bukkit.World#spawnEntity(Location, EntityType)}.
     * <br>
     * This is the same as the {@code ENTITY} argument type, except that this omits the {@code Player} entity type
     */
    ArgumentType<EntityType> SPAWNABLE_ENTITY = new FilteredEnumArgumentType<>(EntityType.class, entityType -> entityType != EntityType.PLAYER, "living entity");

    /**
     * {@code ArgumentType} that represents a loot table
     */
    ArgumentType<LootTable> LOOT_TABLE = new LootTableArgumentType();

    /**
     * {@code ArgumentType} that represents a material
     */
    ArgumentType<Material> MATERIAL = new EnumArgumentType<>(Material.class, "material");

    /**
     * {@code ArgumentType} that represents a material where {@link Material#isBlock()} is {@code true}
     */
    ArgumentType<Material> BLOCK = new FilteredEnumArgumentType<>(Material.class, Material::isBlock, "block");

    /**
     * {@code ArgumentType} that represents a material where {@link Material#isItem()} is {@code true}
     */
    ArgumentType<Material> ITEM = new FilteredEnumArgumentType<>(Material.class, Material::isItem, "item");

    /**
     * {@code ArgumentType} that represents a player that's online
     */
    ArgumentType<Player> ONLINE_PLAYER = new PlayerArgumentType();

    /**
     * {@code ArgumentType} that represents a {@code CommandCore} command
     */
    ArgumentType<Command> COMMAND = new CommandArgumentType();

    /**
     * Creates an {@code ArgumentType} that represents a number
     * @param numberClass The number class. This class must either be a primitive or a primitive wrapper.
     *                    For example, {@code int.class}, {@code Long.class}
     * @return The newly created {@code ArgumentType}
     * @param <T> The type of the number
     * @throws IllegalArgumentException If {@code numberClass} isn't a primitive class or a primitive wrapper
     */
    @Contract("_ -> new")
    static @NotNull <T extends Number> ArgumentType<T> number(Class<T> numberClass) {
        return new NumberArgumentType<>(numberClass);
    }

    /**
     * Creates an {@code ArgumentType} that represents a number with a minimum value (inclusive)
     * @param numberClass The number class. This class must either be a primitive or a primitive wrapper.
     *                    For example, {@code int.class}, {@code Long.class}
     * @param min The inclusive minimum value
     * @return The newly created {@code ArgumentType}
     * @param <T> The type of the number
     */
    @Contract("_, _ -> new")
    static @NotNull <T extends Number & Comparable<T>> ArgumentType<T> rangedNumber(Class<T> numberClass, T min) {
        return new RangedArgumentType<>(numberClass, min);
    }
    /**
     * Creates an {@code ArgumentType} that represents a number with a minimum (inclusive) and maximum (inclusive) value
     * @param numberClass The number class. This class must either be a primitive or a primitive wrapper.
     *                    For example, {@code int.class}, {@code Long.class}
     * @param min The inclusive minimum value
     * @param max The inclusive maximum value
     * @return The newly created {@code ArgumentType}
     * @param <T> The type of the number
     */
    @Contract("_, _, _ -> new")
    static @NotNull <T extends Number & Comparable<T>> ArgumentType<T> rangedNumber(Class<T> numberClass, T min, T max) {
        return new RangedArgumentType<>(numberClass, min, max);
    }

    /**
     * Creates an {@code ArgumentType} that represents a deserialized json value.
     * If the type you are deserializing is generic, use the method {@link #json(Class, Type)} instead.
     * @param clazz The class of the object that the json will be deserialized into
     * @return The deserialized object
     * @param <T> The type of the deserialized object
     */
    static @NotNull <T> ArgumentType<T> json(Class<T> clazz) {
        return new JsonArgumentType<>(clazz);
    }
    /**
     * Creates an {@code ArgumentType} that represents a deserialized json value.
     * This method should be used if the type that is being deserialized is generic. If it isn't, use the method {@link #json(Class)} instead.
     * @param clazz The class of the object that the json will be deserialized into
     * @param type The type of the object that the json will be deserialized into.
     *             You can get this type by using a {@link TypeToken}.
     *             For example, to get the type of {@code Set<String>}, you should use
     *             <pre>
     *             {@code
     *             Type stringSetType = new TypeToken<Set<String>>() {}.getType();
     *             }
     *             </pre>
     * @return The deserialized object
     * @param <T> The type of the deserialized object
     * @see Gson#fromJson(String, Type)
     */
    static @NotNull <T> ArgumentType<T> json(Class<T> clazz, Type type) {
        return new JsonArgumentType<>(clazz, type);
    }

    /**
     * Gets the name of this argument type
     * @return The name
     */
    @NotNull
    String getName();

    /**
     * Attempts to parse an {@code ArgumentReader}.
     * After parsing, the reader will always be on the space of the next argument, or the end of the reader
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
     * Gets the class that this argument parses into
     * @return The class
     */
    @NotNull
    Class<T> getArgumentClass();

    /**
     * Represents a custom {@code ArgumentType} that parses to an enum value
     * @param <T> The type of the enum
     */
    class EnumArgumentType<T extends Enum<T>> implements SimpleArgumentType<T> {
        private final Class<T> enumClass;
        private final String enumName;
        private final List<String> tabCompletes;
        private final boolean uppercaseValues;

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
            this.enumClass = enumClass;
            this.enumName = enumName;
            this.tabCompletes = Arrays.stream(enumClass.getEnumConstants()).map(val -> val.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());

            for (T enumConstant : enumClass.getEnumConstants()) {
                for (char letter : enumConstant.name().toCharArray()) {
                    if (Character.isLetter(letter) && !Character.isUpperCase(letter)) {
                        this.uppercaseValues = false;
                        Bukkit.getLogger().warning("[CommandCore] Enum " + enumName + " includes values that aren't in uppercase!");
                        return;
                    }
                }
            }
            this.uppercaseValues = true;
        }

        @Override
        public @NotNull String getName() {
            return enumName;
        }

        @Override
        public @NotNull Result<T, None> parseWord(String word) {
            return Result.resolve(() -> uppercaseValues ? Enum.valueOf(enumClass, word.toUpperCase()) : EnumUtils.findEnumInsensitiveCase(enumClass, word));
        }

        @Override
        public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
            return tabCompletes;
        }

        @Override
        public @NotNull Class<T> getArgumentClass() {
            return enumClass;
        }
    }

    /**
     * Represents a custom {@code ArgumentType} that parses to a filtered enum
     * @param <T> The type of the enum
     */
    class FilteredEnumArgumentType<T extends Enum<T>> extends EnumArgumentType<T> {
        private final Predicate<T> filter;
        private final List<String> tabCompletes;

        /**
         * Creates a new {@code ArgumentType}
         * @param enumClass The enum's class
         * @param filter The filter that enum values must pass through
         * @param enumName The name of the enum. This is used when displaying an error message (Invalid {{@code enumName}} '{val}'
         */
        public FilteredEnumArgumentType(@NotNull Class<T> enumClass, Predicate<T> filter, String enumName) {
            super(enumClass, enumName);
            this.filter = filter;
            this.tabCompletes = Arrays.stream(enumClass.getEnumConstants()).filter(filter).map(val -> val.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());
        }

        @Override
        public @NotNull Result<T, None> parseWord(String word) {
            return super.parseWord(word).andThen(val -> filter.test(val) ? Result.ok(val) : Result.error());
        }

        @Override
        public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
            return tabCompletes;
        }
    }
}
