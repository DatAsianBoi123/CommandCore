package com.datasiqn.commandcore.commands;

import com.datasiqn.commandcore.arguments.Arguments;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface CommandExecutor {
    /**
     * Executes this command executor
     * @param sender The sender that executed the command
     * @param args The command arguments
     * @return The output of the command
     */
    CommandOutput execute(@NotNull CommandSender sender, @NotNull Arguments args);

    /**
     * Gets the tabcomplete for this command executor
     * @param sender The sender that requested the tab completions
     * @param args The command arguments
     * @return The tab completions
     */
    @NotNull
    default List<String> tabComplete(@NotNull CommandSender sender, @NotNull Arguments args) {
        return new ArrayList<>();
    }
}
