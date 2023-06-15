package com.datasiqn.commandcore.commands;

import com.datasiqn.commandcore.commands.context.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Represents an executor for a command
 */
public interface CommandExecutor {
    /**
     * Executes this command executor
     * @param context The context in which this command was executed
     * @return The result of the command
     */
    @NotNull Result<None, List<String>> execute(@NotNull CommandContext context);

    /**
     * Gets the tabcomplete for this command executor
     * @param context The context in which this command was tab completed
     * @return The tab completions
     */
    default @NotNull TabComplete getTabComplete(@NotNull CommandContext context) {
        return new TabComplete(Collections.emptyList(), context.getArguments().getString(context.getArguments().size() - 1));
    }
}
