package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.ExecutableCommand;
import com.datasiqn.commandcore.argument.annotation.Block;
import com.datasiqn.commandcore.argument.annotation.Item;
import com.datasiqn.commandcore.argument.annotation.Limit;
import com.datasiqn.commandcore.argument.annotation.Living;
import com.datasiqn.commandcore.argument.annotation.Name;
import com.datasiqn.commandcore.argument.annotation.QuotedWord;
import com.datasiqn.commandcore.argument.annotation.Spawnable;
import com.datasiqn.commandcore.argument.annotation.Word;
import com.datasiqn.commandcore.argument.numrange.NumberRange;
import com.datasiqn.commandcore.argument.selector.EntitySelector;
import com.datasiqn.commandcore.argument.selector.SelectorRequirements;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.commandcore.command.annotation.AnnotationCommand;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.resultapi.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.loot.LootTable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents an argument type.
 * Most builtin {@code ArgumentType}s found as static fields/methods in this interface are registered.
 * Unregistered {@code ArgumentType}s are still usable in {@link CommandBuilder}s, however they need to be registered to be used in an {@link AnnotationCommand}.
 * @param <T> The type of the argument
 */
public interface ArgumentType<T> {
    //<editor-fold desc="Builtin Argument Types">

    /**
     * {@code ArgumentType} that is just a single word.
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link Word} annotation to use.
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
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link QuotedWord} annotation to use.
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
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link Name} annotation to use.
     */
    ArgumentType<String> NAME = new NameArgumentType();

    /**
     * {@code ArgumentType} that represents a boolean.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotations to use.
     */
    ArgumentType<Boolean> BOOLEAN = new BoolArgumentType();

    /**
     * {@code ArgumentType} that represents a {@link java.util.UUID UUID}.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotations to use.
     */
    ArgumentType<java.util.UUID> UUID = new UuidArgumentType();

    /**
     * {@code ArgumentType} that represents a {@link NamespacedKey}.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotations to use.
     */
    ArgumentType<NamespacedKey> NAMESPACED_KEY = new NamespacedKeyArgumentType();

    /**
     * {@code ArgumentType} that represents a vector.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotations to use.
     */
    ArgumentType<Vector> VECTOR = new VectorArgumentType();

    /**
     * {@code ArgumentType} that represents a loaded world.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotations to use.
     */
    ArgumentType<World> WORLD = new WorldArgumentType();

    /**
     * {@code ArgumentType} that represents an entity type. This includes all enum constants from {@link EntityType}, except it omits {@link EntityType#UNKNOWN}.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotation to use.
     */
    ArgumentType<EntityType> ENTITY_TYPE = new FilteredEnumArgumentType<>(EntityType.class, entityType -> entityType != EntityType.UNKNOWN, "entity");

    /**
     * {@code ArgumentType} that represents an entity that is living.
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link Living} annotation to use.
     */
    ArgumentType<EntityType> LIVING_ENTITY_TYPE = new FilteredEnumArgumentType<>(EntityType.class, EntityType::isAlive, "living entity");

    /**
     * {@code ArgumentType} that represents an entity that can be spawned using {@link org.bukkit.World#spawnEntity(Location, EntityType)}.
     * <p>
     * This is the same as the {@code ENTITY} argument type, except this omits {@link EntityType#UNKNOWN} as well as {@link EntityType#PLAYER}.
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link Spawnable} annotation to use.
     */
    ArgumentType<EntityType> SPAWNABLE_ENTITY_TYPE = new FilteredEnumArgumentType<>(EntityType.class, entityType -> entityType != EntityType.PLAYER && entityType != EntityType.UNKNOWN, "entity");

    /**
     * {@code ArgumentType} that represents a loot table.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotations to use.
     */
    ArgumentType<LootTable> LOOT_TABLE = new LootTableArgumentType();

    /**
     * {@code NamespacedKey} that represents a recipe.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotations to use.
     */
    ArgumentType<Recipe> RECIPE = new RecipeArgumentType();

    /**
     * {@code ArgumentType} that represents a material.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotations to use.
     */
    ArgumentType<Material> MATERIAL = new EnumArgumentType<>(Material.class, "material");

    /**
     * {@code ArgumentType} that represents a material where {@link Material#isBlock()} is {@code true}.
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link Block} annotation to use.
     */
    ArgumentType<Material> BLOCK = new FilteredEnumArgumentType<>(Material.class, Material::isBlock, "block");

    /**
     * {@code ArgumentType} that represents a material where {@link Material#isItem()} is {@code true}.
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link Item} annotation to use.
     */
    ArgumentType<Material> ITEM = new FilteredEnumArgumentType<>(Material.class, Material::isItem, "item");

    /**
     * {@code ArgumentType} that represents a player that's online.
     * <p>
     * This is NOT a registered {@code ArgumentType}. See the deprecation notice.
     * @deprecated Use {@link #PLAYER} instead
     */
    @Deprecated
    ArgumentType<Player> ONLINE_PLAYER = new PlayerArgumentType();

    /**
     * {@code ArgumentType} that represents an offline player.
     * <p>
     * This is NOT a registered {@code ArgumentType}.
     */
    ArgumentType<CompletableFuture<OfflinePlayer>> OFFLINE_PLAYER = new OfflinePlayerArgumentType();

    /**
     * {@code ArgumentType} that represents an entity selector that selects just one entity.
     * This is the same as calling {@link #entitySelector(SelectorRequirements) entitySelector}({@link SelectorRequirements}.{@link SelectorRequirements#allowOne(Class) allowOne}(Entity.class)).
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link Limit} annotation to use. See {@link #entitySelector(SelectorRequirements) entitySelector} for more details.
     */
    ArgumentType<EntitySelector<Entity>> ENTITY = entitySelector(SelectorRequirements.allowOne(Entity.class));

    /**
     * {@code ArgumentType} that represents an entity selector that can select any number of entities.
     * This is the same as calling {@link #entitySelector(SelectorRequirements) entitySelector}({@link SelectorRequirements}.{@link SelectorRequirements#allowInfinite(Class) allowInfinite}(Entity.class)).
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link Limit} annotation to use. See {@link #entitySelector(SelectorRequirements) entitySelector} for more details.
     */
    ArgumentType<EntitySelector<Entity>> ENTITIES = entitySelector(SelectorRequirements.allowInfinite(Entity.class));

    /**
     * {@code ArgumentType} that represents an entity selector that selects just one player.
     * This is the same as calling {@link #entitySelector(SelectorRequirements) entitySelector}({@link SelectorRequirements}.{@link SelectorRequirements#allowOne(Class) allowOne}(Player.class)).
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link Limit} annotation to use. See {@link #entitySelector(SelectorRequirements) entitySelector} for more details.
     */
    ArgumentType<EntitySelector<Player>> PLAYER = entitySelector(SelectorRequirements.allowOne(Player.class));

    /**
     * {@code ArgumentType} that represents an entity selector that can select any number of players.
     * This is the same as calling {@link #entitySelector(SelectorRequirements) entitySelector}({@link SelectorRequirements}.{@link SelectorRequirements#allowInfinite(Class) allowInfinite}(Player.class)).
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link Limit} annotation to use. See {@link #entitySelector(SelectorRequirements) entitySelector} for more details.
     */
    ArgumentType<EntitySelector<Player>> PLAYERS = entitySelector(SelectorRequirements.allowInfinite(Player.class));

    /**
     * {@code ArgumentType} that represents a {@code CommandCore} command name.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotations to use.
     */
    ArgumentType<Command> COMMAND_NAME = new CommandNameArgumentType();

    /**
     * {@code ArgumentType} that represents an executable command.
     * <p>
     * Note: due to the way vanilla commands are executed, there will be no tabcompletes for those commands. However, custom plugin commands will have tabcompletes.
     * Both types of commands will still be able to execute.
     * <p>
     * This is NOT a registered {@code ArgumentType}.
     */
    ArgumentType<ExecutableCommand> COMMAND = new CommandArgumentType();

    /**
     * Creates an {@code ArgumentType} that represents a number.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotations to use.
     * @param numberClass The number class. This class must either be a primitive or a primitive wrapper.
     *                    For example, {@code int.class}, {@code Long.class}
     * @return The newly created {@code ArgumentType}
     * @param <T> The type of the number
     * @throws IllegalArgumentException If {@code numberClass} isn't a primitive class or a primitive wrapper
     */
    @Contract("_ -> new")
    static @NotNull <T extends Number> ArgumentType<T> number(Class<T> numberClass) {
        return NumberArgumentType.number(numberClass);
    }

    /**
     * Creates an {@code ArgumentType} that represents a number with a minimum value (inclusive).
     * <p>
     * This is a registered {@code ArgumentType} and requires any {@code Bounded} annotation to use.
     * The annotation must match the type of the number ({@code T}).
     * <pre>
     * Ex.
     *      &#64;BoundedDouble  double  -> boundedNumber(double)
     *      &#64;BoundedLong    int     -> int // annotation is ignored because the number types don't match
     *      &#64;BoundedLong    long    -> boundedNumber(long)
     * </pre>
     * @param numberClass The number class. This class must either be a primitive or a primitive wrapper.
     *                    For example, {@code int.class}, {@code Long.class}.
     * @param min The inclusive minimum value
     * @return The newly created {@code ArgumentType}
     * @param <T> The type of the number
     * @throws IllegalArgumentException If {@code numberClass} isn't a primitive class or a primitive wrapper
     */
    @Contract("_, _ -> new")
    static @NotNull <T extends Number & Comparable<T>> ArgumentType<T> boundedNumber(Class<T> numberClass, T min) {
        return new BoundedNumberArgumentType<>(numberClass, min);
    }
    /**
     * Creates an {@code ArgumentType} that represents a number with a minimum (inclusive) and maximum (inclusive) value.
     * <p>
     * This is a registered {@code ArgumentType} and requires any {@code Bounded} annotation to use.
     * The annotation must match the type of the number ({@code T}).
     * <pre>
     * Ex.
     *      &#64;BoundedDouble  double  -> boundedNumber(double)
     *      &#64;BoundedLong    int     -> int // annotation is ignored because the number types don't match
     *      &#64;BoundedLong    long    -> boundedNumber(long)
     * </pre>
     * @param numberClass The number class. This class must either be a primitive or a primitive wrapper.
     *                    For example, {@code int.class}, {@code Long.class}.
     * @param min The inclusive minimum value
     * @param max The inclusive maximum value
     * @return The newly created {@code ArgumentType}
     * @param <T> The type of the number
     * @throws IllegalArgumentException If {@code numberClass} isn't a primitive class or a primitive wrapper
     */
    @Contract("_, _, _ -> new")
    static @NotNull <T extends Number & Comparable<T>> ArgumentType<T> boundedNumber(Class<T> numberClass, T min, T max) {
        return new BoundedNumberArgumentType<>(numberClass, min, max);
    }

    /**
     * Creates an {@code ArgumentType} that represents a range. Ranges are typed with a double dot (..) format.
     * <p>
     * This is a registered {@code ArgumentType} and does not require any annotations to use.
     * There must be a type argument present on {@link NumberRange}.
     * <p>
     * <pre>
     *     {@code
     *
     *     5        Range that includes any number, x, such that x == 5
     *     0..      Range that includes any number, x, such that 0 <= x
     *     ..0      Range that includes any number, x, such that x <= 0
     *     -3..5    Range that includes any number, x, such that -3 <= x <= 5
     *     ..       Range that includes any number
     *
     *     }
     * </pre>
     * @param numberClass The number class. This class must either be a primitive or a primitive wrapper.
     *                    For example, {@code int.class}, {@code Long.class}.
     * @return The newly created {@code ArgumentType}
     * @param <T> The type of the number
     * @throws IllegalArgumentException If {@code numberClass} isn't a primitive class or a primitive wrapper
     */
    @Contract("_ -> new")
    static @NotNull <T extends Number & Comparable<T>> ArgumentType<NumberRange<T>> numberRange(Class<T> numberClass) {
        return new NumberRangeArgumentType<>(numberClass);
    }

    /**
     * Creates an {@code ArgumentType} that represents a deserialized json value.
     * If the type you are deserializing is generic, use the method {@link #json(Class, Type)} instead.
     * <p>
     * This is NOT a registered {@code ArgumentType}.
     * @param clazz The class of the object that the json will be deserialized into
     * @return The newly created {@code ArgumentType}
     * @param <T> The type of the deserialized object
     */
    static @NotNull <T> ArgumentType<T> json(Class<T> clazz) {
        return new JsonArgumentType<>(clazz);
    }
    /**
     * Creates an {@code ArgumentType} that represents a deserialized json value.
     * This method should be used if the type that is being deserialized is generic. If it isn't, use the method {@link #json(Class)} instead.
     * <p>
     * This is NOT a registered {@code ArgumentType}.
     * @param clazz The class of the object that the json will be deserialized into
     * @param type The type of the object that the json will be deserialized into.
     *             You can get this type by using a {@link TypeToken}.
     *             For example, to get the type of {@code Set<String>}, you should use
     *             <pre>
     *             {@code
     *             Type stringSetType = new TypeToken<Set<String>>() {}.getType();
     *             }
     *             </pre>
     * @return The newly created {@code ArgumentType}
     * @param <T> The type of the deserialized object
     * @see Gson#fromJson(String, Type)
     */
    static @NotNull <T> ArgumentType<T> json(Class<T> clazz, Type type) {
        return new JsonArgumentType<>(clazz, type);
    }

    /**
     * Creates an {@code ArgumentType} that represents an entity selector with the requirements of {@code requirements}.
     * <p>
     * This is a registered {@code ArgumentType} that requires the {@link Limit} annotation to use.
     * There must be a type argument present on {@link EntitySelector}. The type provided will be the limiting entity class.
     * For example, to select only {@code Players}, you would use an {@code EntitySelector<Player>}.
     * @param requirements The requirements that the entity selector must conform to
     * @return The newly created {@code ArgumentType}
     * @param <E> The type of the entities being selected
     */
    static @NotNull <E extends Entity> ArgumentType<EntitySelector<E>> entitySelector(SelectorRequirements<E> requirements) {
        return new EntitySelectorArgumentType<>(requirements);
    }

    //</editor-fold>

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
        return Collections.emptyList();
    }

    /**
     * Gets the class that this argument parses into
     * @return The class
     */
    @NotNull
    Class<T> getArgumentClass();
}
