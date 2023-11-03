package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

class BoolArgumentType implements SimpleArgumentType<Boolean> {
    @Override
    public @NotNull String getName() {
        return "boolean";
    }

    @Override
    public @NotNull Result<Boolean, None> parseWord(@NotNull String word) {
        if (word.equalsIgnoreCase("true")) return Result.ok(true);
        else if (word.equalsIgnoreCase("false")) return Result.ok(false);
        return Result.error();
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return Arrays.asList("true", "false");
    }

    @Override
    public @NotNull Class<Boolean> getArgumentClass() {
        return Boolean.class;
    }
}
