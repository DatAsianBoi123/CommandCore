package com.datasiqn.commandcore.argument;

import org.jetbrains.annotations.NotNull;

// TODO: Write documentation
public class StringArgumentReader implements ArgumentReader {
    private final String arg;

    private int index = 0;

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
        return splice(beginning, arg.length());
    }

    @Override
    public @NotNull String splice(int beginning, int end) {
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

}
