package com.datasiqn.commandcore.argument.selector;

import org.bukkit.entity.EntityType;

/**
 * Represents a collection of selector types. This is the character sent after an at (@) symbol, which have builtin {@link SelectorOptions} that can be overridden by the user.
 */
public enum SelectorType {
    /**
     * Selects the first nearest player
     * <p>
     * {@code @p}
     */
    NEAREST_PLAYER('p', new SelectorOptions()
            .set(SelectorOptionType.LIMIT, 1)
            .set(SelectorOptionType.SORT, EntityOrders.NEAREST)
            .set(SelectorOptionType.TYPE, EntityType.PLAYER)),
    /**
     * Selects a random player
     * <p>
     * {@code @r}
     */
    RANDOM_PLAYER('r', new SelectorOptions()
            .set(SelectorOptionType.LIMIT, 1)
            .set(SelectorOptionType.SORT, EntityOrders.RANDOM)
            .set(SelectorOptionType.TYPE, EntityType.PLAYER)),
    /**
     * Selects all players
     * <p>
     * {@code @a}
     */
    ALL_PLAYERS('a', new SelectorOptions()
            .set(SelectorOptionType.TYPE, EntityType.PLAYER)),
    /**
     * Selects all entities
     * <p>
     * {@code @e}
     */
    ALL_ENTITIES('e', new SelectorOptions()),
    ;

    private final char c;
    private final SelectorOptions defaultOptions;

    SelectorType(char c, SelectorOptions defaultOptions) {
        this.c = c;
        this.defaultOptions = defaultOptions;
    }

    /**
     * Gets the character typed after the at (@) symbol
     * @return The character
     */
    public char getChar() {
        return c;
    }

    /**
     * Gets the default options that this selector type has
     * @return A copy of the default selector options
     */
    public SelectorOptions getDefaultOptions() {
        return defaultOptions.copy();
    }
}
