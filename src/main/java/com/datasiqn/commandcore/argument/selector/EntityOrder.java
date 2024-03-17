package com.datasiqn.commandcore.argument.selector;

import com.datasiqn.commandcore.command.source.CommandSource;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

/**
 * Represents an operation that modifies and sorts a list of entities.
 * A collection of these can be found in the {@link EntityOrders} class.
 */
@FunctionalInterface
public interface EntityOrder {
    /**
     * Orders a list of entities based on {@code source}
     * @param source The source that is sorting the entities, or {@code null} if there is none
     * @param entities The list of entities that are sorted
     */
    void order(@Nullable CommandSource source, @NotNull List<Entity> entities);

    /**
     * Creates a new {@code EntityOrder} that sorts using an {@code EntityOrderingComparator}
     * @param comparator The comparator to use when sorting
     * @return The newly created {@code EntityOrder}
     */
    @Contract(pure = true)
    static @NotNull EntityOrder comparator(EntityOrderComparator comparator) {
        return (source, entities) -> entities.sort((e1, e2) -> comparator.compare(source, e1, e2));
    }

    /**
     * A comparing function that is used to sort a list of entities
     * @see Comparator
     */
    @FunctionalInterface
    interface EntityOrderComparator {
        /**
         * Compares 2 entities together. Behaves the same way as {@link Comparator#compare(Object, Object)}
         * @param source The source that is sorting the entities, or {@code null} if there is none
         * @param e1 The first entity to compare
         * @param e2 The second entity to compare
         * @return A number denoting which entity is "above" the other
         * @see Comparator#compare(Object, Object)
         */
        int compare(@Nullable CommandSource source, Entity e1, Entity e2);
    }
}
