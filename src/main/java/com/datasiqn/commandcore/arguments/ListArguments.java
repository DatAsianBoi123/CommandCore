package com.datasiqn.commandcore.arguments;

import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListArguments implements Arguments {
    protected final List<String> allArguments;

    /**
     * Creates a new {@code ListArguments}
     * @param args The arguments in a string list
     */
    public ListArguments(List<String> args) {
        allArguments = args;
    }

    @Override
    public int size() {
        return allArguments.size();
    }

    @Override
    public @NotNull <T> Result<T, String> get(int i, ArgumentType<T> type) {
        if (i >= allArguments.size()) throw new IllegalArgumentException("i is greater than total length of arguments");
        return type.parse(new StringArgumentReader(allArguments.get(i)));
    }

    @Override
    public @NotNull String getString(int i) {
        return get(i, ArgumentType.NAME).unwrapOr("");
    }

    @Override
    public @NotNull ArgumentReader asReader() {
        return new StringArgumentReader(String.join(" ", allArguments));
    }
}
