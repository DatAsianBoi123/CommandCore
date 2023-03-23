package com.datasiqn.commandcore.arguments;

import org.jetbrains.annotations.NotNull;

// TODO: Write documentation for this class
public interface ArgumentReader {
    char get();

    char next();

    boolean atEnd();

    int index();

    int size();

    @NotNull
    String section(int beginning);
    @NotNull
    String section(int beginning, int end);

    @NotNull
    String nextWord();

    @NotNull
    ArgumentReader copy();
}
