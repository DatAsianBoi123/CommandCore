package com.datasiqn.commandcore.locatable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a command sender that can be located
 */
public interface LocatableCommandSender {
    /**
     * Gets the location of this object
     * @return A clone of location
     */
    @NotNull
    Location getLocation();

    /**
     * Gets which world this object is located in
     * @return The world
     */
    @NotNull
    World getWorld();

    /**
     * Gets the sender of the command that got executed
     * @return The sender
     */
    @NotNull
    CommandSender getSender();
}
