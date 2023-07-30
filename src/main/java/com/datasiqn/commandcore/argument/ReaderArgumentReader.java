package com.datasiqn.commandcore.argument;

import com.mojang.brigadier.StringReader;
import org.jetbrains.annotations.NotNull;

public class ReaderArgumentReader implements ArgumentReader {
    private final StringReader argumentReader;

    public ReaderArgumentReader(StringReader argumentReader) {
        this.argumentReader = argumentReader;
    }

    @Override
    public char get() {
        return argumentReader.peek();
    }

    @Override
    public char next() {
        return argumentReader.read();
    }

    @Override
    public int index() {
        return argumentReader.getCursor();
    }

    @Override
    public int size() {
        return argumentReader.getTotalLength();
    }

    @Override
    public void jumpTo(int index) {
        argumentReader.setCursor(index);
    }

    @Override
    public @NotNull String substring(int beginning) {
        return argumentReader.getString().substring(beginning);
    }

    @Override
    public @NotNull String substring(int beginning, int end) {
        return argumentReader.getString().substring(beginning, end);
    }
}
