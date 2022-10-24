package com.datasiqn.commandcore.arguments;

import com.datasiqn.commandcore.ArgumentParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface Arguments {
    int size();

    @NotNull
    <T> T get(int i, ArgumentType<T> type) throws ArgumentParseException;

    @NotNull
    String getString(int i);

    @Unmodifiable @NotNull List<String> asList();
}
