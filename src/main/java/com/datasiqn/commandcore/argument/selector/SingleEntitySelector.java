package com.datasiqn.commandcore.argument.selector;

import com.datasiqn.commandcore.command.source.CommandSource;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Represents a selection of just 1 entity. Any entity selector that does not start with an at (@) symbol is a {@code SingleEntitySelector}.
 * @param <E> The type of the selected entity
 */
public class SingleEntitySelector<E extends Entity> implements EntitySelector<E> {
    private final E entity;

    /**
     * Creates a new {@code SingleEntitySelector} with the selected entity of {@code entity}
     * @param entity The entity that was selected
     */
    public SingleEntitySelector(E entity) {
        this.entity = entity;
    }

    @Override
    public E getFirst(@Nullable CommandSource source) {
        return entity;
    }

    @Override
    public @NotNull List<E> get(@Nullable CommandSource source) {
        return Collections.singletonList(entity);
    }
}
