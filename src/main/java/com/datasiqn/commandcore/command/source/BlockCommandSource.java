package com.datasiqn.commandcore.command.source;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.locatable.LocatableBlockSender;
import com.datasiqn.commandcore.locatable.LocatableCommandSender;
import com.datasiqn.resultapi.Result;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@code CommandSource} where the sender is a {@link BlockCommandSender}
 * <br><br>
 * <strong>NOTE: Do not directly instantiate this class! Instead, use the factory method {@link CommandCore#createSource(CommandSender) createSource}</strong>
 */
public class BlockCommandSource implements CommandSource {
    private final BlockCommandSender sender;

    /**
     * Creates a new {@code BlockCommandSource} with the internal sender of {@code sender}.
     * <br><br>
     * <strong>NOTE: Do not directly use this constructor! Instead, use the factory method {@link CommandCore#createSource(CommandSender) createSource}</strong>
     * @param sender The {@code BlockCommandSender} to internally use
     */
    public BlockCommandSource(BlockCommandSender sender) {
        this.sender = sender;
    }

    @Override
    public @NotNull Result<BlockCommandSender, String> getBlockChecked() {
        return Result.ok(sender);
    }

    @Override
    public @NotNull Result<LocatableCommandSender, String> getLocatableChecked() {
        return Result.ok(new LocatableBlockSender(sender));
    }

    @Override
    public @NotNull CommandSender sender() {
        return sender;
    }
}
