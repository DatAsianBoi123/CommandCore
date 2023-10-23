package com.datasiqn.commandcore.argument;

import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a list of arguments that are already parsed
 */
public class StringArguments implements Arguments {
    private final List<String> allArguments;

    /**
     * Creates a new {@code ListArguments}
     * @param args The arguments in a string list
     */
    public StringArguments(List<String> args) {
        allArguments = args;
    }

    @Override
    public int size() {
        return allArguments.size();
    }

    @Override
    public @NotNull <T> Result<T, String> getChecked(int i, @NotNull ArgumentType<T> type) {
        checkBounds(i);
        return type.parse(new StringArgumentReader(allArguments.get(i)));
    }

    @Override
    public @NotNull String getString(int i) {
        checkBounds(i);
        return allArguments.get(i);
    }

    @Override
    public @NotNull ArgumentReader asReader() {
        return new StringArgumentReader(String.join(" ", allArguments));
    }

    private void checkBounds(int i) {
        if (i >= size()) throw new IndexOutOfBoundsException("index (" + i + ") is greater than total size (" + size() + ")");
        if (i < 0) throw new IndexOutOfBoundsException("index cannot be negative");
    }
}
