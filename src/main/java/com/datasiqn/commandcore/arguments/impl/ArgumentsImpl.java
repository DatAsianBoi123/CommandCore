package com.datasiqn.commandcore.arguments.impl;

import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.arguments.Arguments;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArgumentsImpl implements Arguments {
    protected final List<String> allArguments;

    public ArgumentsImpl(List<String> args) {
        allArguments = args;
    }

    public int size() {
        return allArguments.size();
    }

    public @NotNull <T> Result<T, String> get(int i, ArgumentType<T> type) {
        if (i >= allArguments.size()) throw new IllegalArgumentException("i is greater than total length of arguments");
        return type.parse(allArguments.get(i));
    }

    @Override
    public @NotNull String getString(int i) {
        return get(i, ArgumentType.STRING).unwrapOr("");
    }
}
