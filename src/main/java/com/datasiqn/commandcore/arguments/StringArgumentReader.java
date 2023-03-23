package com.datasiqn.commandcore.arguments;

import org.jetbrains.annotations.NotNull;

// TODO: Write documentation
public class StringArgumentReader implements ArgumentReader {
    private final String arg;

    private int index;

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
        return index + 1 == arg.length();
    }

    @Override
    public @NotNull String section(int beginning) {
        return section(beginning, arg.length() - 1);
    }
    @Override
    public @NotNull String section(int beginning, int end) {
        return arg.substring(beginning, end);
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
    public @NotNull String nextWord() {
        StringBuilder builder = new StringBuilder();
        builder.append(get());
        while (index + 1 < arg.length() && next() != ' ') {
            builder.append(get());
        }
        return builder.toString();
    }

    @Override
    public @NotNull ArgumentReader copy() {
        StringArgumentReader reader = new StringArgumentReader(arg);
        reader.index = index;
        return reader;
    }
}
