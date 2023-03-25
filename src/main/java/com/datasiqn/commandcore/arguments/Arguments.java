package com.datasiqn.commandcore.arguments;

import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

/**
 * Represents command arguments
 */
public interface Arguments {
    /**
     * Gets the size of the arguments
     * @return The number of arguments
     */
    int size();

    /**
     * Gets a specific argument
     * @param i    The index of the argument
     * @param type The argument type
     * @param <T>  The type of the argument type
     * @return The result of the parsing
     * @throws IllegalArgumentException If {@code i} is an invalid index ({@code i} {@literal <} 0, or {@code i} {@literal >} {@link Arguments#size()})
     */
    @NotNull <T> Result<T, String> get(int i, ArgumentType<T> type);

    /**
     * Gets a simple string argument
     * @param i The index of the argument
     * @return The argument as a string
     */
    @NotNull String getString(int i);

    //TODO: Write javadocs
    @NotNull ArgumentReader asReader();
}
