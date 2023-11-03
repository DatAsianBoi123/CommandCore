package com.datasiqn.commandcore.command;

import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.command.source.CommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the context in which a command is executed
 */
public record CommandContext(@NotNull CommandSource source, @NotNull Command command, @NotNull String label, @NotNull Arguments arguments) {
}
