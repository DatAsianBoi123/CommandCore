package com.datasiqn.commandcore.arguments;

import com.datasiqn.commandcore.ArgumentParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents command arguments
 * This is more or less a wrapper for a {@code List<String>}
 */
public interface Arguments {
    /**
     * Gets the size of the arguments
     * @return The number of arguments
     */
    int size();

    /**
     * Gets a specific argument
     * @param i The index of the argument
     * @param type The argument type
     * @return The argument
     * @param <T> The type of the argument type
     * @throws ArgumentParseException If the argument could not be parsed
     * @throws IllegalArgumentException If {@code i} is an invalid index ({@code i} < 0, or {@code i} > {@link Arguments#size()})
     */
    @NotNull
    <T> T get(int i, ArgumentType<T> type) throws ArgumentParseException;

    /**
     * Gets a simple string argument
     * @param i The index of the argument
     * @return The argument as a string
     */
    @NotNull
    String getString(int i);

    /**
     * Gets the arguments represented as a list of strings
     * @return An unmodifiable view of all arguments
     */
    @UnmodifiableView @NotNull List<String> asList();
}
