package com.datasiqn.commandcore.commands.context.impl;

import com.datasiqn.commandcore.commands.CommandSource;
import com.datasiqn.resultapi.Result;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandSourceImpl implements CommandSource {
    private final CommandSender sender;

    public CommandSourceImpl(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public @NotNull Result<Player, IllegalStateException> getPlayer() {
        return Result.resolve(() -> (Player) sender, error -> new IllegalStateException("Sender is not a player"));
    }

    @Override
    public @NotNull Result<Entity, IllegalStateException> getEntity() {
        return Result.resolve(() -> (Entity) sender, error -> new IllegalStateException("Sender is not an entity"));
    }

    @Override
    public @NotNull CommandSender getSender() {
        return sender;
    }
}
