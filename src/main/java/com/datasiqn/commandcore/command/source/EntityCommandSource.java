package com.datasiqn.commandcore.command.source;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.locatable.LocatableCommandSender;
import com.datasiqn.commandcore.locatable.LocatableEntitySender;
import com.datasiqn.resultapi.Result;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@code CommandSource} where the sender is an {@link Entity}.
 * <br><br>
 * <strong>NOTE: Do not directly instantiate this class! Instead, use the factory method {@link CommandCore#createSource(CommandSender) createSource}</strong>
 */
public class EntityCommandSource implements CommandSource {
    private final Entity sender;

    /**
     * Creates a new {@code EntityCommandSource} with the internal sender of {@code sender}.
     * <br><br>
     * <strong>NOTE: Do not directly use this constructor! Instead, use the factory method {@link CommandCore#createSource(CommandSender) createSource}</strong>
     * @param sender The sender
     */
    public EntityCommandSource(Entity sender) {
        this.sender = sender;
    }

    @Override
    public @NotNull Result<Entity, String> getEntityChecked() {
        return Result.ok(sender);
    }

    @Override
    public @NotNull Result<LocatableCommandSender, String> getLocatableChecked() {
        return Result.ok(new LocatableEntitySender(sender));
    }

    @Override
    public @NotNull CommandSender getSender() {
        return sender;
    }
}
