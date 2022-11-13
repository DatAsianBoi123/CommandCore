package com.datasiqn.commandcore.commands.context;

import com.datasiqn.commandcore.arguments.Arguments;
import com.datasiqn.commandcore.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the context in which a command is executed
 */
public interface CommandContext {
    /**
     * Gets the sender that executed this command
     *
     * @return The sender
     */
    @NotNull CommandSource getSource();

    /**
     * Gets all arguments represented as a list of strings
     * @return All arguments
     */
    @NotNull Arguments getArguments();
}
