package com.datasiqn.commandcore.argument;

import com.datasiqn.commandcore.argument.type.ArgumentType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a prepared command that can be executed with a {@link CommandSender}.
 * This is used in {@link ArgumentType#COMMAND}.
 */
public interface ExecutableCommand {
    /**
     * Executes the command with a sender of {@code sender}
     * @param sender The sender that executed the command
     */
    void execute(@NotNull CommandSender sender);
}
