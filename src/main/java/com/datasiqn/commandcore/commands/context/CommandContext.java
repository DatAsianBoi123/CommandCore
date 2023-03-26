package com.datasiqn.commandcore.commands.context;

import com.datasiqn.commandcore.arguments.Arguments;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the context in which a command is executed
 */
// TODO: documentation
public interface CommandContext {
    /**
     * Gets the sender that executed this command
     * @return The sender
     */
    @NotNull CommandSource getSource();

    @NotNull Command getCommand();

    @NotNull String getLabel();

    /**
     * Gets all command arguments
     * @return All arguments
     */
    @NotNull Arguments getArguments();
}
