package com.datasiqn.commandcore.argument;

import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents arguments that have already been parsed
 */
public class ParsedArguments implements Arguments {
    private final List<ParsedArgument<?>> arguments;
    private final String stringArguments;

    /**
     * Creates new {@code ParsedArguments} with the internal arguments of {@code arguments}
     * @param arguments The internal arguments to use
     */
    public ParsedArguments(@NotNull List<ParsedArgument<?>> arguments) {
        this.arguments = arguments;
        this.stringArguments = String.join(" ", arguments.stream().map(ParsedArgument::stringArg).toList());
    }

    @Override
    public int size() {
        return arguments.size();
    }

    @Override
    public @NotNull <T> Result<T, String> getChecked(int i, @NotNull ArgumentType<T> type) {
        Arguments.checkBounds(i, size());
        ParsedArgument<?> argument = arguments.get(i);
        Class<?> argClass = argument.arg.getClass();
        if (!type.getArgumentClass().isAssignableFrom(argClass)) {
            String className = argClass.getName();
            String typeName = type.getName();
            String typeClassName = type.getArgumentClass().getName();
            return Result.error("argument is of type " + className + ", but tried to get it with argument type " + typeName + " (" + typeClassName + ")");
        }
        return Result.ok(type.getArgumentClass().cast(argument.arg));
    }

    @Override
    public @NotNull String getString(int i) {
        Arguments.checkBounds(i, size());
        return arguments.get(i).stringArg;
    }

    @Override
    public @NotNull ArgumentReader asReader() {
        return new StringArgumentReader(stringArguments);
    }

    /**
     * Represents one argument that has already been parsed
     * @param <T> The type of the argument
     */
    public record ParsedArgument<T>(T arg, String stringArg) {
        /**
         * Creates a new {@code ParsedArgument} with the argument of {@code arg} and the string argument of {@code stringArg}
         * @param arg The already parsed argument
         * @param stringArg The string argument. This is what the user types in the command.
         */
        public ParsedArgument { }
    }
}
