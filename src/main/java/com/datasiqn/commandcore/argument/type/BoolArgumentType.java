package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.commandcore.util.ParseUtil;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

class BoolArgumentType implements SimpleArgumentType<Boolean> {
    @Override
    public @NotNull String getTypeName() {
        return "boolean";
    }

    @Override
    public @NotNull Result<Boolean, None> parseWord(@NotNull String word) {
        return Result.resolve(() -> ParseUtil.strictParseBoolean(word));
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return Arrays.asList("true", "false");
    }
}
