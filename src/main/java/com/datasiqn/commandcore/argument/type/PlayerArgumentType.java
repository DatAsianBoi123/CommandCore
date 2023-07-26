package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

class PlayerArgumentType implements SimpleArgumentType<Player> {
    @Override
    public @NotNull String getTypeName() {
        return "player";
    }

    @Override
    public @NotNull Result<Player, None> parseWord(String word) {
        return Result.ofNullable(Bukkit.getPlayerExact(word), None.NONE);
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}
