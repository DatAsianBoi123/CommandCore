package com.datasiqn.commandcore.argument;

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
     * @param i The index of the argument
     * @param type The argument type
     * @param <T> The type of the argument type
     * @return The result of the parsing
     * @throws IndexOutOfBoundsException If {@code i} is an invalid index ({@code i} {@literal <} 0, or {@code i} {@literal >=} {@link #size()})
     */
    @NotNull <T> Result<T, String> get(int i, ArgumentType<T> type);

    /**
     * Gets a simple string argument
     * @param i The index of the argument
     * @return The argument as a string
     * @throws IndexOutOfBoundsException if {@code i} is an invalid index ({@code i} {@literal  <} 0, or {@code i} {@literal  >=} {@link #size()}
     */
    @NotNull String getString(int i);

    /**
     * Converts all arguments into an {@code ArgumentReader}
     * @return The newly created {@code ArgumentReader}
     */
    @NotNull ArgumentReader asReader();
}
