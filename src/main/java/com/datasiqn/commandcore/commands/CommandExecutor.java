package com.datasiqn.commandcore.commands;

import com.datasiqn.commandcore.commands.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface CommandExecutor {
    /**
     * Executes this command executor
     * @param context The context in which this command was executed
     * @return The output of the command
     */
    @NotNull CommandOutput execute(@NotNull CommandContext<CommandSender> context);

    /**
     * Gets the tabcomplete for this command executor
     * @param context The context in which this command was tab completed
     * @return The tab completions
     */
    @NotNull
    default List<String> tabComplete(@NotNull CommandContext<CommandSender> context) {
        return new ArrayList<>();
    }
}
