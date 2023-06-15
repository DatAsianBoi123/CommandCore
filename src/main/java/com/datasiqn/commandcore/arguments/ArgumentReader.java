package com.datasiqn.commandcore.arguments;

import org.jetbrains.annotations.NotNull;

// TODO: Write documentation for this class
public interface ArgumentReader {
    char get();

    char next();

    boolean atEnd();

    int index();

    int size();

    void jumpTo(int index);

    @NotNull String splice(int beginning);
    @NotNull String splice(int beginning, int end);

    @NotNull String nextWord();
}
