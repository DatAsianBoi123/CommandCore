package com.datasiqn.commandcore.argument;

import com.datasiqn.commandcore.argument.type.ArgumentType;
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
     * @return The parsed argument
     * @throws IllegalArgumentException If the argument could not be parsed into {@code type}
     * @throws IndexOutOfBoundsException If {@code i} is an invalid index ({@code i} {@literal <} 0, or {@code i} {@literal >=} {@link #size()})
     */
    default @NotNull <T> T get(int i, ArgumentType<T> type) {
        return getChecked(i, type).<IllegalArgumentException>unwrapOrThrow(err -> new IllegalArgumentException("argument could not be parsed: " + err));
    }

    /**
     * Same as {@link #get(int, ArgumentType)}, except checks if there is an error and returns a {@code Result}
     * @param i The index of the argument
     * @param type The argument type
     * @param <T> The type of the argument type
     * @return The result of the parsing
     * @throws IndexOutOfBoundsException If {@code i} is an invalid index ({@code i} {@literal <} 0, or {@code i} {@literal >=} {@link #size()})
     */
    @NotNull <T> Result<T, String> getChecked(int i, ArgumentType<T> type);

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

    /**
     * Checks if {@code i} is smaller than {@code size} and at least 0
     * @param i The number to check
     * @param size The total size
     * @throws IndexOutOfBoundsException If {@code i} {@literal >=} {@code size} or {@code i} {@literal <} 0
     */
    static void checkBounds(int i, int size) {
        if (i >= size) throw new IndexOutOfBoundsException("index (" + i + ") is greater than total size (" + size + ")");
        if (i < 0) throw new IndexOutOfBoundsException("index cannot be negative");
    }
}
