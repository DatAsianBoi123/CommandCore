package com.datasiqn.commandcore.argument.selector;

import com.datasiqn.commandcore.argument.numrange.FullNumberRange;
import com.datasiqn.commandcore.argument.numrange.NumberRange;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.argument.type.EnumArgumentType;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * Contains a collection of builtin {@link SelectorOption}s that the user can input when creating a {@link MultiEntitySelector}
 * @param <T> The type that is contained in the {@link SelectorOption}
 */
public final class SelectorOptionType<T> {
    private static final Map<String, SelectorOptionType<?>> ALL_OPTIONS = new HashMap<>();

    /**
     * Filters the found entities by matching its name. Default value is {@code null}.
     */
    public static final SelectorOptionType<String> NAME = new SelectorOptionType<>("name", ArgumentType.NAME, null);
    /**
     * Limits how many entities can be selected. Default value is {@link Integer#MAX_VALUE}.
     */
    public static final SelectorOptionType<Integer> LIMIT = new SelectorOptionType<>("limit", ArgumentType.boundedNumber(int.class, 1), Integer.MAX_VALUE);
    /**
     * Sorts the found entities based on an {@link EntityOrder}. Default value is {@link EntityOrders#ARBITRARY}.
     */
    public static final SelectorOptionType<EntityOrders> SORT = new SelectorOptionType<>("sort", new EnumArgumentType<>(EntityOrders.class), EntityOrders.ARBITRARY);
    /**
     * Filters the found entities by matching its type. Default value is {@code null}, meaning all types.
     */
    public static final SelectorOptionType<EntityType> TYPE = new SelectorOptionType<>("type", ArgumentType.ENTITY_TYPE, null);
    /**
     * Filters the found entities by matching its distance from the source. Default value is {@code null}, which is used instead of {@link FullNumberRange} to prevent distance calculations.
     */
    public static final SelectorOptionType<NumberRange<Double>> DISTANCE = new SelectorOptionType<>("distance", ArgumentType.numberRange(double.class), null);
    /**
     * Filters the found entities by matching the world it's in. Default value is {@code null}, meaning all worlds.
     */
    public static final SelectorOptionType<World> WORLD = new SelectorOptionType<>("world", ArgumentType.WORLD, null);
    /**
     * Filters the found entities by matching its difference in x from the source. Default value is {@link FullNumberRange}.
     */
    public static final SelectorOptionType<NumberRange<Double>> DX = new SelectorOptionType<>("dx", ArgumentType.numberRange(double.class), new FullNumberRange<>());
    /**
     * Filters the found entities by matching its difference in y from the source. Default value is {@link FullNumberRange}.
     */
    public static final SelectorOptionType<NumberRange<Double>> DY = new SelectorOptionType<>("dy", ArgumentType.numberRange(double.class), new FullNumberRange<>());
    /**
     * Filters the found entities by matching its difference in z from the source. Default value is {@link FullNumberRange}.
     */
    public static final SelectorOptionType<NumberRange<Double>> DZ = new SelectorOptionType<>("dz", ArgumentType.numberRange(double.class), new FullNumberRange<>());

    private final String name;
    private final ArgumentType<T> argumentType;
    private final T def;

    /**
     * <strong>DO NOT INSTANTIATE!</strong>
     * <p>
     * By instantiating this class, it is added onto a static map of options. If multiple instantiations happen, memory leaks can occur.
     */
    private SelectorOptionType(String name, ArgumentType<T> argumentType, T def) {
        this.name = name;
        this.argumentType = argumentType;
        this.def = def;

        ALL_OPTIONS.put(name, this);
    }

    /**
     * Sets some value into {@code options} by using this {@code SelectorOptionType}.
     * <p>
     * <strong>Note:</strong> This is an inherently unsafe method, as there is no type checking being performed. Make sure that {@code value} is the same type as {@code T}.
     * @param options The options to insert {@code value} into
     * @param value The value that should be inserted into {@code options}. This should have the same type as {@code E}.
     */
    public void uncheckedSet(@NotNull SelectorOptions options, Object value) {
        //noinspection unchecked
        options.getOption(this).set((T) value);
    }

    /**
     * Creates the default option for this option type
     * @return The newly created {@code SelectorOption}
     */
    @Contract(value = " -> new", pure = true)
    public @NotNull SelectorOption<T> createOption() {
        return new SelectorOption<>(name, def);
    }

    /**
     * Gets the name of this option type
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the argument type used to parse values
     * @return The argument type
     */
    public ArgumentType<T> getArgumentType() {
        return argumentType;
    }

    /**
     * Gets all the options that have been created
     * @return An unmodifiable view of all the created option types
     */
    @Contract(pure = true)
    public static @NotNull @UnmodifiableView Map<String, SelectorOptionType<?>> getAllOptions() {
        return Collections.unmodifiableMap(ALL_OPTIONS);
    }
}
