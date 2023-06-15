package com.datasiqn.commandcore.command.context;

import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.CommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the context in which a command is executed
 */
public interface CommandContext {
    /**
     * Gets the sender that executed this command
     * @return The sender
     */
    @NotNull CommandSource getSource();

    /**
     * Gets the command that was executed
     * @return The command
     */
    @NotNull Command getCommand();

    /**
     * Gets the string used to execute the command. This could the name of the command, or an alias of the command.
     * @return The exact string used to execute the command
     */
    @NotNull String getLabel();

    /**
     * Gets all command arguments
     * @return All arguments
     */
    @NotNull Arguments getArguments();
}
