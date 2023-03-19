package com.datasiqn.commandcore.commands.context.impl;

import com.datasiqn.commandcore.commands.CommandSource;
import com.datasiqn.resultapi.Result;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandSourceImpl implements CommandSource {
    private final CommandSender sender;

    public CommandSourceImpl(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public @NotNull Result<Player, String> getPlayer() {
        return Result.resolve(() -> (Player) sender, error -> "Sender is not a player");
    }

    @Override
    public @NotNull Result<Entity, String> getEntity() {
        return Result.resolve(() -> (Entity) sender, error -> "Sender is not an entity");
    }

    @Override
    public @NotNull CommandSender getSender() {
        return sender;
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return getSender().hasPermission(permission);
    }
    @Override
    public boolean hasPermission(@Nullable String permission) {
        return permission == null || getSender().hasPermission(permission);
    }
}
