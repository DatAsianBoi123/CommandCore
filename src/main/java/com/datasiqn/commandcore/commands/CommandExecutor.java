package com.datasiqn.commandcore.commands;

import com.datasiqn.commandcore.commands.context.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    @NotNull
    default List<String> tabComplete(@NotNull CommandContext context) {
        return new ArrayList<>();
    }
}
