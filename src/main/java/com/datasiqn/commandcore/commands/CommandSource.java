package com.datasiqn.commandcore.commands;

import com.datasiqn.resultapi.Result;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

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

    @NotNull CommandSender getSender();

    boolean hasPermission(Permission permissible);

    boolean hasPermission(String permission);
}
