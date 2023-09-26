package com.datasiqn.commandcore.locatable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a block that can be located
 */
public class LocatableBlockSender implements LocatableCommandSender {
    private final BlockCommandSender blockSender;

    /**
     * Constructs a new {@code LocatableBlock} with an internal command sender of {@code blockSender}
     * @param blockSender The internal blockSender to use
     */
    public LocatableBlockSender(BlockCommandSender blockSender) {
        this.blockSender = blockSender;
    }

    @Override
    public @NotNull Location getLocation() {
        return blockSender.getBlock().getLocation();
    }

    @Override
    public @NotNull World getWorld() {
        return blockSender.getBlock().getWorld();
    }

    @Override
    public @NotNull CommandSender getSender() {
        return blockSender;
    }
}
