package com.datasiqn.commandcore.argument.type;

import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

class IntArgumentType implements SimpleArgumentType<Integer> {
    @Override
    public @NotNull String getName() {
        return "integer";
    }

    @Override
    public @NotNull Result<Integer, None> parseWord(@NotNull String word) {
        return Result.resolve(() -> Integer.parseInt(word));
    }

    @Override
    public @NotNull Class<Integer> getArgumentClass() {
        return Integer.class;
    }
}
