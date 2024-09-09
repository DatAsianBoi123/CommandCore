package com.datasiqn.commandcore.argument.selector;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a list of selector options. This is used when creating an {@link MultiEntitySelector}.
 */
public class SelectorOptions {
    private final Map<String, Object> options = new HashMap<>();

    /**
     * Gets the value of a selector option
     * @param type The selector option type
     * @return The found selector option
     * @param <T> The type of the selector option
     */
    public <T> @NotNull T get(@NotNull SelectorOptionType<T> type) {
        options.putIfAbsent(type.getName(), type.getDef());
        //noinspection unchecked
        return (T) options.get(type.getName());
    }

    /**
     * Sets the value of a selector option
     * @param type The selector option type
     * @param value The value to set it to
     * @return {@code this}, for chaining
     * @param <T> The type of the selector option
     */
    public <T> SelectorOptions set(@NotNull SelectorOptionType<T> type, T value) {
        options.put(type.getName(), value);
        return this;
    }

    @Override
    public String toString() {
        String optionString = options.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(","));
        return "SelectorOptions[" + optionString + "]";
    }

    /**
     * Creates a copy of {@code this}
     * @return The copy
     */
    public SelectorOptions copy() {
        SelectorOptions copy = new SelectorOptions();
        copy.options.putAll(options);
        return copy;
    }
}
