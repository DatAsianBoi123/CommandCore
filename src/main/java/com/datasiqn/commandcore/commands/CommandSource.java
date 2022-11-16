package com.datasiqn.commandcore.commands;

import com.datasiqn.resultapi.Result;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

public interface CommandSource {
    @NotNull Result<Player, IllegalStateException> getPlayer();

    @NotNull Result<Entity, IllegalStateException> getEntity();

    @NotNull CommandSender getSender();

    boolean hasPermission(Permission permissible);

    boolean hasPermission(String permission);
}
