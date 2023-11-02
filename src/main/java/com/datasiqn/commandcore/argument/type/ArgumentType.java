package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
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
     * {@code ArgumentType} that represents the name of something. Can have multiple spaces in the name
     */
    ArgumentType<String> NAME = new NameArgumentType();

    /**
     * {@code ArgumentType} that represents an integer
     */
    ArgumentType<Integer> INTEGER = new IntArgumentType();

    /**
     * {@code ArgumentType} that represents an integer that is no smaller than 1
     */
    ArgumentType<Integer> NATURAL_NUMBER = rangedInt(1);

    /**
     * {@code ArgumentType} that represents a double
     */
    ArgumentType<Double> DOUBLE = new DoubleArgumentType();

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
     * {@code ArgumentType} that represents a player
     */
    ArgumentType<Player> PLAYER = new PlayerArgumentType();

    /**
     * {@code ArgumentType} that represents a {@code CommandCore} command
     */
    ArgumentType<Command> COMMAND = new CommandArgumentType();

    /**
     * Creates an {@code ArgumentType} that represents an integer with a minimum value (inclusive)
     * @param min The inclusive minimum value
     * @return The newly created {@code ArgumentType}
     */
    @Contract("_ -> new")
    static @NotNull ArgumentType<Integer> rangedInt(int min) {
        return new RangedIntArgumentType(min);
    }
    /**
     * Creates an {@code ArgumentType} that represents an integer with a minimum (inclusive) and maximum (inclusive) value
     * @param min The inclusive minimum value
     * @param max The inclusive maximum value
     * @return The newly created {@code ArgumentType}
     */
    @Contract("_, _ -> new")
    static @NotNull ArgumentType<Integer> rangedInt(int min, int max) {
        return new RangedIntArgumentType(min, max);
    }

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
        public @NotNull String getTypeName() {
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
