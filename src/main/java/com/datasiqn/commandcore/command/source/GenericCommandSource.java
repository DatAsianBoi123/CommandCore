package com.datasiqn.commandcore.command.source;

import com.datasiqn.commandcore.CommandCore;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a generic command source that is none of the other command sources.
 * <br><br>
 * <strong>Do not directly instantiate this class! Instead, use the factory method {@link CommandCore#createSource(CommandSender) createSource}</strong>
 */
public record GenericCommandSource(CommandSender sender) implements CommandSource {
    /**
     * Creates a new {@code GenericCommandSource} with the internal sender of {@code sender}.
     * <br><br>
     * <strong>Do not directly use constructor! Instead, use the factory method {@link CommandCore#createSource(CommandSender) createSource}</strong>
     * @param sender The command sender
     */
    public GenericCommandSource { }

    @Override
    public @NotNull CommandSender getSender() {
        return sender;
    }
}
