package com.datasiqn.commandcore.argument;

import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a list of arguments.
 * <p>
 * Internally, this uses a {@code List<String>} to store arguments and parses the string it every time the user wants an argument.
 */
public class StringArguments implements Arguments {
    private final List<String> allArguments;
    private final String stringArguments;

    /**
     * Creates a new {@code ListArguments}
     * @param args The arguments in a string list
     */
    public StringArguments(List<String> args) {
        allArguments = args;
        stringArguments = String.join(" ", allArguments);
    }

    @Override
    public int size() {
        return allArguments.size();
    }

    @Override
    public @NotNull <T> Result<T, String> getChecked(int i, @NotNull ArgumentType<T> type) {
        Arguments.checkBounds(i, size());
        return type.parse(new StringArgumentReader(allArguments.get(i)));
    }

    @Override
    public @NotNull String getString(int i) {
        Arguments.checkBounds(i, size());
        return allArguments.get(i);
    }

    @Override
    public @NotNull ArgumentReader asReader() {
        return new StringArgumentReader(stringArguments);
    }

}
