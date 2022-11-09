package com.datasiqn.commandcore.commands.context;

import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.arguments.Arguments;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * Represents the context in which a command is executed
 * @param <S> The type of the sender that executed the command
 */
public interface CommandContext<S extends CommandSender> {
    /**
     * Gets the sender that executed this command
     * @return The sender
     */
    @NotNull S getSender();

    /**
     * Gets all arguments represented as a list of strings
     * @return All arguments
     */
    @NotNull Arguments getArguments();
}
