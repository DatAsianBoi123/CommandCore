package com.datasiqn.commandcore.argument.type;

import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PlayerArgumentType implements SimpleArgumentType<Player> {
    @Override
    public @NotNull String getTypeName() {
        return "player";
    }

    @Override
    public @NotNull Result<Player, None> parseWord(String word) {
        return Result.ofNullable(Bukkit.getPlayerExact(word), None.NONE);
    }
}
