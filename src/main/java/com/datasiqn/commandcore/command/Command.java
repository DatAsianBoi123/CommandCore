package com.datasiqn.commandcore.command;

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

    /**
     * Gets the aliases of this command
     * @return The aliases
     */
    @NotNull String[] getAliases();
}
