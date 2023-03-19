package com.datasiqn.commandcore.commands;

import com.datasiqn.commandcore.commands.builder.CommandLink;
import com.datasiqn.resultapi.Result;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

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
     * Gets whether the source of the command has a permission or not
     * @param permission The permission
     * @return True if the source has {@code permission}, false otherwise
     */
    boolean hasPermission(Permission permission);
    /**
     * Gets whether the source of the command has a permission or not
     * @param permission The permission
     * @return True if the source has {@code permission}, false otherwise
     */
    boolean hasPermission(String permission);
}
