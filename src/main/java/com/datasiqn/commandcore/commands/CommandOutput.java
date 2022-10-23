package com.datasiqn.commandcore.commands;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class CommandOutput {
    private final CommandResult result;
    private final String message;

    private CommandOutput(CommandResult result) {
        this(result, "");
    }
    private CommandOutput(CommandResult result, String message) {
        this.result = result;
        this.message = message;
    }

    public CommandResult getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull CommandOutput success() {
        return new CommandOutput(CommandResult.SUCCESS);
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull CommandOutput failure() {
        return failure("Incorrect usage!");
    }
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull CommandOutput failure(String message) {
        return new CommandOutput(CommandResult.FAILURE, message);
    }
}
