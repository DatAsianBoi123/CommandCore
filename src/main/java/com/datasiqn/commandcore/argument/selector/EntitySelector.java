package com.datasiqn.commandcore.argument.selector;

import com.datasiqn.commandcore.command.source.CommandSource;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a selection of entities
 * @param <E> The type of the entity
 */
public interface EntitySelector<E extends Entity> {
    /**
     * Gets the first entity found by this {@code EntitySelector}, or {@code null} if one wasn't found.
     * <p>
     * <strong>Note:</strong> This operation can be quite expensive, so the result of this method should be cached if possible.
     * @param source The source that is finding the entities, or {@code null} if there is none
     * @return The first entity found by this {@code EntitySelector}, or {@code null} if one wasn't found
     */
    default E getFirst(@Nullable CommandSource source) {
        List<E> entities = get(source);
        if (entities.isEmpty()) return null;
        return entities.get(0);
    }

    /**
     * Finds all the entities found by this {@code EntitySelector}
     * <p>
     * <strong>Note:</strong> This operation can be quite expensive, so the result of this method should be cached if possible.
     * @param source The source that is finding the entities, or {@code null} if there is none
     * @return A list of all the entities found by this {@code EntitySelector}
     */
    @NotNull List<E> get(@Nullable CommandSource source);
}
