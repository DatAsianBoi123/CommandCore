package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.Result;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

class VectorArgumentType implements ArgumentType<Vector> {
    @Override
    public @NotNull Result<Vector, String> parse(@NotNull ArgumentReader reader) {
        Result<Integer, String> x = INTEGER.parse(reader);
        if (x.isError()) return Result.error(x.unwrapError());
        if (reader.atEnd()) return Result.error("Expected 3 integers for a location, but got 1 instead");
        reader.next();

        Result<Integer, String> y = INTEGER.parse(reader);
        if (y.isError()) return Result.error(y.unwrapError());
        if (reader.atEnd()) return Result.error("Expected 3 integers for a location, but got 2 instead");
        reader.next();

        Result<Integer, String> z = INTEGER.parse(reader);
        if (z.isError()) return Result.error(z.unwrapError());
        if (!reader.atEnd()) reader.next();

        return Result.ok(new Vector(x.unwrap(), y.unwrap(), z.unwrap()));
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        Result<Player, String> player = context.source().getPlayerChecked();
        if (player.isError()) return Collections.emptyList();
        Block targetBlock = player.unwrap().getTargetBlockExact(5);
        if (targetBlock == null) return Collections.emptyList();
        Vector vector = targetBlock.getLocation().toVector();
        return Collections.singletonList(vector.getBlockX() + " " + vector.getBlockY() + " " + vector.getBlockZ());
    }
}
