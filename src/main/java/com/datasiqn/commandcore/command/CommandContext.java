package com.datasiqn.commandcore.command;

import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.command.source.CommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the context in which a command is executed
 */
public record CommandContext(@NotNull CommandSource source, @NotNull Command command, @NotNull String label, @NotNull Arguments arguments) {
    /**
     * Creates a new command context
     * @param source The source of the command
     * @param command The command that is being executed
     * @param label The label that the user used when executing this command. This could either be the actual command name, or any aliases of the command.
     * @param arguments The arguments that the command was executed with
     */
    public CommandContext { }
}
