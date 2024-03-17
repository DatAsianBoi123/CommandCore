package com.datasiqn.commandcore.argument.selector;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a list of {@link SelectorOption}s. This is used when creating an {@link MultiEntitySelector}.
 */
public class SelectorOptions {
    private final Map<String, SelectorOption<?>> options = new HashMap<>();

    /**
     * Gets the value of a {@link SelectorOption}
     * @param type The selector option type used to find the {@link SelectorOption}
     * @return The value contained inside the found {@link SelectorOption}
     * @param <T> The type of the value contained inside the {@link SelectorOption}
     */
    public <T> T get(@NotNull SelectorOptionType<T> type) {
        return getOption(type).get();
    }

    /**
     * Sets the value of a {@link SelectorOption}
     * @param type The selector option type used to find the {@link SelectorOption}
     * @param value The value to set it to
     * @return {@code this}, for chaining
     * @param <T> The type of the value contained inside the {@link SelectorOption}
     */
    public <T> SelectorOptions set(@NotNull SelectorOptionType<T> type, T value) {
        getOption(type).set(value);
        return this;
    }

    /**
     * Gets the {@code SelectorOption} that has the type of {@code type}
     * @param type The selector option typed used to find the {@code SelectorOption}
     * @return The found {@code SelectorOption}
     * @param <T> The type of the value contained inside the {@link SelectorOption}
     */
    public <T> @NotNull SelectorOption<T> getOption(@NotNull SelectorOptionType<T> type) {
        SelectorOption<T> option = type.createOption();
        options.putIfAbsent(option.getName(), option);
        //noinspection unchecked
        return (SelectorOption<T>) options.get(option.getName());
    }

    @Override
    public String toString() {
        String optionString = options.values().stream().map(option -> option.getName() + "=" + option.get()).collect(Collectors.joining(","));
        return "SelectorOptions[" + optionString + "]";
    }

    /**
     * Creates a copy of {@code this}
     * @return The copy
     */
    public SelectorOptions copy() {
        SelectorOptions copy = new SelectorOptions();
        options.forEach((name, option) -> copy.options.put(name, new SelectorOption<>(option.getName(), option.get())));
        return copy;
    }
}
