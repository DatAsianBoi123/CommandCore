package com.datasiqn.commandcore.argument.selector;

/**
 * Represents a selector option. A list of these can be found in the {@link SelectorOptionType} class.
 * @param <T> The type of the option
 */
public class SelectorOption<T> {
    private final String name;

    private T value;

    SelectorOption(String name, T value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the value that this {@code SelectorOption} contains
     * @return The value
     */
    public T get() {
        return value;
    }

    /**
     * Sets the value that this {@code SelectorOption} contains
     * @param value The new value
     */
    public void set(T value) {
        this.value = value;
    }

    /**
     * Gets the name of this {@code SelectorOption}
     * @return The name
     */
    public String getName() {
        return name;
    }
}
