package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class LootTableArgumentType implements SimpleArgumentType<LootTable> {
    private final List<String> tabCompletes = Arrays.stream(LootTables.values()).map(LootTables::name).collect(Collectors.toList());

    @Override
    public @NotNull String getName() {
        return "loot table";
    }

    @Override
    public @NotNull Result<LootTable, None> parseWord(String word) {
        return Result.resolve(() -> LootTables.valueOf(word.toUpperCase())).map(LootTables::getLootTable);
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return tabCompletes;
    }

    @Override
    public @NotNull Class<LootTable> getArgumentClass() {
        return LootTable.class;
    }
}
