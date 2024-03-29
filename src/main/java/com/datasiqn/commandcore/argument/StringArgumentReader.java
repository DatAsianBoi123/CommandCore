package com.datasiqn.commandcore.argument;

import org.jetbrains.annotations.NotNull;

/**
 * An {@code ArgumentReader} that reads a string
 */
public class StringArgumentReader implements ArgumentReader {
    private final String arg;

    private int index = 0;

    /**
     * Creates a new {@code ArgumentReader} that reads a string
     * @param arg The string to read
     */
    public StringArgumentReader(String arg) {
        this.arg = arg;
    }

    @Override
    public char get() {
        return arg.charAt(index);
    }

    @Override
    public char next() {
        index++;
        if (index >= arg.length()) throw new IllegalStateException("reached end of string input");
        return get();
    }

    @Override
    public boolean atEnd() {
        return index + 1 >= arg.length();
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public int size() {
        return arg.length();
    }

    @Override
    public void jumpTo(int index) {
        if (index >= size()) throw new IndexOutOfBoundsException("index (" + index + ") is greater than length (" + size() + ")");
        if (index < 0) throw new IndexOutOfBoundsException("index cannot be negative");
        this.index = index;
    }

    @Override
    public @NotNull String splice(int beginning) {
        return splice(beginning, size());
    }
    @Override
    public @NotNull String splice(int beginning, int end) {
        if (end > size()) throw new IndexOutOfBoundsException("end index (" + end + ") is larger than the length (" + size() + ")");
        if (beginning < 0) throw new IndexOutOfBoundsException("beginning index cannot be negative");
        if (beginning > end) throw new IndexOutOfBoundsException("beginning index cannot be larger than end index");
        return arg.substring(beginning, end);
    }

    @Override
    public @NotNull String nextWord() {
        if (atEnd()) return String.valueOf(get());
        StringBuilder builder = new StringBuilder();
        builder.append(get());
        while (!atEnd() && next() != ' ') {
            builder.append(get());
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return arg;
    }
}
