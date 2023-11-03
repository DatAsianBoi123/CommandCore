package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an {@code ArgumentType} that just parses a single word
 * @param <T> The type of the argument
 */
public interface SimpleArgumentType<T> extends ArgumentType<T> {
    /**
     * Gets the argument type name
     * @return The argument type name
     */
    @NotNull String getTypeName();

    /**
     * Parses just a single word
     * @param word The word
     * @return A {@code Result} containing the parsed value
     */
    @NotNull Result<T, None> parseWord(String word);

    @Override
    default @NotNull Result<T, String> parse(@NotNull ArgumentReader reader) {
        String word = reader.nextWord();
        return parseWord(word).mapError(none -> "Invalid " + getTypeName() + " '" + word + "'");
    }
}
