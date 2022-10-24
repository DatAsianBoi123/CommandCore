package com.datasiqn.commandcore.arguments.impl;

import com.datasiqn.commandcore.ArgumentParseException;
import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.arguments.Arguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public class ArgumentsImpl implements Arguments {
    protected final List<String> allArguments;

    public ArgumentsImpl(List<String> args) {
        allArguments = args;
    }

    public int size() {
        return allArguments.size();
    }

    public <T> @NotNull T get(int i, ArgumentType<T> type) throws ArgumentParseException {
        if (i >= allArguments.size()) throw new IllegalArgumentException("i is greater than total length of arguments");
        return type.parse(allArguments.get(i));
    }

    @Override
    public @NotNull String getString(int i) {
        try {
            return get(i, ArgumentType.STRING);
        } catch (ArgumentParseException e) {
            return "";
        }
    }

    public @Unmodifiable @NotNull List<String> asList() {
        return Collections.unmodifiableList(allArguments);
    }
}
