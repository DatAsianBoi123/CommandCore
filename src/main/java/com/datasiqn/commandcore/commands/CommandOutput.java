package com.datasiqn.commandcore.commands;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the output of a command
 */
public class CommandOutput {
    private final CommandResult result;
    private final String[] message;

    private CommandOutput(CommandResult result) {
        this(result, "");
    }
    private CommandOutput(CommandResult result, String... messages) {
        this.result = result;
        this.message = messages;
    }

    /**
     * Gets the result of this command
     * @return The command result
     */
    public CommandResult getResult() {
        return result;
    }

    /**
     * Get the messages of this command
     * @return The messages only if the result is {@link CommandResult#FAILURE}, null otherwise
     */
    public String[] getMessages() {
        return message;
    }

    /**
     * Creates a new {@link CommandOutput} with a result of {@link CommandResult#SUCCESS}
     * @return The created {@link CommandOutput} instance
     */
    @Contract(value = " -> new", pure = true)
    public static @NotNull CommandOutput success() {
        return new CommandOutput(CommandResult.SUCCESS);
    }

    /**
     * Creates a new {@link CommandOutput} with a result of {@link CommandResult#FAILURE} and a message of {@code Incorrect usage!}
     * @return The created {@link CommandOutput} instance
     */
    @Contract(value = " -> new", pure = true)
    public static @NotNull CommandOutput failure() {
        return failure("Incorrect usage!");
    }

    /**
     * Creates a new {@link CommandOutput} with a result of {@link CommandResult#FAILURE}
     * @param messages The error messages
     * @return The created {@link CommandOutput} instance
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull CommandOutput failure(String... messages) {
        return new CommandOutput(CommandResult.FAILURE, messages);
    }
}
