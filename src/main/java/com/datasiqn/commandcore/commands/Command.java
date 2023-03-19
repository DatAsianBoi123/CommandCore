package com.datasiqn.commandcore.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a command
 */
public interface Command {
    /**
     * Gets the executor for this command
     * @return The executor
     */
    @NotNull CommandExecutor getExecutor();

    /**
     * Gets the permission for this command, registered in the plugin.yml file
     * @return The permission, or null if it doesn't require one
     */
    @Nullable String getPermissionString();

    /**
     * Gets the description of this command
     * @return The description
     */
    @NotNull String getDescription();

    /**
     * Gets the usages for this command
     * @return The usages
     */
    @NotNull List<String> getUsages();

    @NotNull String[] getAliases();
}
