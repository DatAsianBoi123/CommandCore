package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

class WorldArgumentType implements SimpleArgumentType<World> {
    @Override
    public @NotNull String getTypeName() {
        return "world";
    }

    @Override
    public @NotNull Result<World, None> parseWord(String word) {
        return Result.ofNullable(Bukkit.getWorld(word), None.NONE);
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
    }
}
