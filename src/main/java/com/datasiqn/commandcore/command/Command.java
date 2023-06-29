package com.datasiqn.commandcore.command;

import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Represents a command
 */
public interface Command {
    /**
     * Gets this command's name
     * @return The name
     */
    @NotNull String getName();

    /**
     * Gets the aliases of this command
     * @return The aliases
     */
    @NotNull String @NotNull [] getAliases();

    /**
     * Executes this command executor
     * @param context The context in which this command was executed
     * @return The result of the command
     */
    @NotNull Result<None, List<String>> execute(CommandContext context);

    /**
     * Gets the tabcomplete for this command executor
     * @param context The context in which this command was tab completed
     * @return The tab completions
     */
    default @NotNull TabComplete tabComplete(CommandContext context) {
        // matching string is blank because tab complete is an empty list
        return new TabComplete(Collections.emptyList(), "");
    }

    /**
     * Gets the permission for this command, registered in the plugin.yml file
     * @return The permission, or null if it doesn't require one
     */
    @Nullable String getPermissionString();

    /**
     * Gets whether this command has a permission or not
     * @return {@code true} if this command has a permission, {@code false} otherwise
     */
    boolean hasPermission();

    /**
     * Gets the description of this command
     * @return The description
     */
    @Nullable String getDescription();

    /**
     * Gets whether this command has a description or not
     * @return {@code true} if this command has a description, {@code false} otherwise
     */
    boolean hasDescription();

    /**
     * Gets the usages for this command
     * @return The usages
     */
    @NotNull List<String> getUsages();
}
