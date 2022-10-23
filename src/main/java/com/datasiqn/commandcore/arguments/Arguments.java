package com.datasiqn.commandcore.arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;

public interface Arguments {
    int size();

    @NotNull
    <T> Optional<T> get(int i, ArgumentType<T> type);

    @Unmodifiable @NotNull List<String> asList();
}
