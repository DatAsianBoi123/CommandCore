package com.datasiqn.commandcore.locatable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an entity that can be located
 */
public class LocatableEntitySender implements LocatableCommandSender {
    private final Entity entity;

    /**
     * Constructs a new {@code LocatableEntity} with an internal command sender of {@code entity}
     * @param entity The internal entity to use
     */
    public LocatableEntitySender(Entity entity) {
        this.entity = entity;
    }

    @Override
    public @NotNull Location getLocation() {
        return entity.getLocation();
    }

    @Override
    public @NotNull World getWorld() {
        return entity.getWorld();
    }

    @Override
    public @NotNull CommandSender getSender() {
        return entity;
    }
}
