package com.datasiqn.commandcore.command;

import com.datasiqn.commandcore.command.builder.CommandLink;
import com.datasiqn.resultapi.Result;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the source of a command
 */
public interface CommandSource {
    /**
     * Gets the player executing command
     * @return A result describing the player. This can be safely unwrapped if the command link {@link CommandLink#requiresPlayer() requiresPlayer}.
     */
    @NotNull
    Result<Player, String> getPlayer();

    /**
     * Gets the entity executing command
     * @return A result describing the entity. This can be safely unwrapped if the command link {@link CommandLink#requiresEntity() requiresEntity}.
     */
    @NotNull
    Result<Entity, String> getEntity();

    /**
     * Gets the sender of the command
     * @return The sender
     */
    @NotNull
    CommandSender getSender();

    /**
     * Sends the command source a message
     * @param messages The messages to send
     */
    default void sendMessage(@NotNull String @NotNull ... messages) {
        getSender().sendMessage(messages);
    }

    /**
     * Gets whether the source of the command has a permission or not
     * @param permission The permission
     * @return {@code true} if the source has {@code permission}, {@code false} otherwise
     */
    default boolean hasPermission(@NotNull Permission permission) {
        return getSender().hasPermission(permission);
    }
    /**
     * Gets whether the source of the command has a permission or not
     * @param permission The permission
     * @return {@code true} if the source has {@code permission}, {@code false} otherwise
     */
    default boolean hasPermission(@Nullable String permission) {
        return permission == null || getSender().hasPermission(permission);
    }
}
