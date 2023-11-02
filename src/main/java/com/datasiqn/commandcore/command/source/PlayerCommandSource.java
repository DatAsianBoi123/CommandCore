package com.datasiqn.commandcore.command.source;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.resultapi.Result;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@code CommandSource} where the sender is a {@link Player}.\
 * <br><br>
 * <strong>NOTE: Do not directly instantiate this class! Instead, use the factory method {@link CommandCore#createSource(CommandSender) createSource}</strong>
 */
public class PlayerCommandSource extends EntityCommandSource {
    private final Player player;

    /**
     * Creates a new {@code CommandSource} with the internal sender of {@code sender}.
     * <br><br>
     * <strong>NOTE: Do not directly use this constructor! Instead, use the factory method {@link CommandCore#createSource(CommandSender) createSource}</strong>
     * @param sender The sender
     */
    public PlayerCommandSource(Player sender) {
        super(sender);
        this.player = sender;
    }

    @Override
    public @NotNull Result<Player, String> getPlayerChecked() {
        return Result.ok(player);
    }
}
