package com.datasiqn.commandcore.commands.context;

import com.datasiqn.commandcore.arguments.ArgumentType;
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
    @NotNull @UnmodifiableView List<String> getArguments();

    /**
     * Parses a specific argument
     * @param type The argument type
     * @param index The index of the argument
     * @return The parsed argument
     * @param <T> The type of the argument type
     * @throws RuntimeException If the argument could not be parsed
     */
    <T> @NotNull T parseArgument(@NotNull ArgumentType<T> type, int index);
}
