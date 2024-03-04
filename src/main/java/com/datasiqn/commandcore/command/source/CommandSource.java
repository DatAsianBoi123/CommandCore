package com.datasiqn.commandcore.command.source;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.command.builder.CommandLink;
import com.datasiqn.commandcore.locatable.LocatableCommandSender;
import com.datasiqn.resultapi.Result;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the source of a command. Get one using the factory method {@link CommandCore#createSource(CommandSender) createSource}.
 */
public interface CommandSource {
    /**
     * Gets the player executing command. This can be safely called if the command link {@link CommandLink#requiresPlayer() requiresPlayer}.
     * @return The player
     * @throws IllegalStateException If the sender is not a player
     */
    default @NotNull Player getPlayer() {
        return getPlayerChecked().<IllegalStateException>unwrapOrThrow(IllegalStateException::new);
    }

    /**
     * Same as {@link #getPlayer()}, except checks if the sender is a player and returns a {@code Result}
     *
     * @return A result describing the player. If the command link {@link CommandLink#requiresPlayer() requiresPlayer}, use {@link #getPlayer()} instead
     */
    @NotNull
    default Result<Player, String> getPlayerChecked() {
        return Result.error("Sender is not a player");
    }

    /**
     * Gets the entity executing command
     * @return The entity. This can be safely called if the command link {@link CommandLink#requiresEntity() requiresEntity}.
     * @throws IllegalStateException If the sender is not an entity
     */
    default @NotNull Entity getEntity() {
        return getEntityChecked().<IllegalStateException>unwrapOrThrow(IllegalStateException::new);
    }

    /**
     * Same as {@link #getEntity()}, except checks if the sender is an entity and returns a {@code Result}
     *
     * @return A result describing the entity. If the command link {@link CommandLink#requiresEntity() requiresEntity}, use {@link #getEntity()} instead
     */
    @NotNull
    default Result<Entity, String> getEntityChecked() {
        return Result.error("Sender is not an entity");
    }

    /**
     * Gets the block executing command
     * @return The block. This can be safely called if the command link {@link CommandLink#requiresBlock() requiresBlock}.
     * @throws IllegalStateException If the sender is not a block
     */
    default @NotNull BlockCommandSender getBlock() {
        return getBlockChecked().<IllegalStateException>unwrapOrThrow(IllegalStateException::new);
    }

    /**
     * Same as {@link #getBlock()}, except checks if the sender is a command block and returns a {@code Result}
     *
     * @return A result describing the command block. If the command link {@link CommandLink#requiresBlock() requiresCommandBlock}, use {@link #getBlock()} instead
     */
    @NotNull
    default Result<BlockCommandSender, String> getBlockChecked() {
        return Result.error("Sender is not a block");
    }

    /**
     * Gets the locatable sender executing command
     * @return The locatable sender. This can be safely called if the command link {@link CommandLink#requiresLocatable() requiresLocatable}.
     * @throws IllegalStateException If the sender is not a locatable sender
     */
    default @NotNull LocatableCommandSender getLocatable() {
        return getLocatableChecked().<IllegalStateException>unwrapOrThrow(IllegalStateException::new);
    }

    /**
     * Same as {@link #getLocatable()}, except checks if the sender is locatable and returns a {@code Result}
     *
     * @return A result describing the locatable sender. If the command link {@link CommandLink#requiresLocatable() requiresCommandBlock}, use {@link #getLocatable()} instead
     */
    @NotNull
    default Result<LocatableCommandSender, String> getLocatableChecked() {
        return Result.error("Sender is not locatable");
    }

    /**
     * Gets the sender of the command
     * @return The sender
     */
    @NotNull CommandSender getSender();

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
