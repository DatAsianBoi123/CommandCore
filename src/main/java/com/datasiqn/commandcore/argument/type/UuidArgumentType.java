package com.datasiqn.commandcore.argument.type;

import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

class UuidArgumentType implements SimpleArgumentType<java.util.UUID> {
    @Override
    public @NotNull String getName() {
        return "UUID";
    }

    @Override
    public @NotNull Result<java.util.UUID, None> parseWord(String word) {
        return Result.resolve(() -> java.util.UUID.fromString(word));
    }

    @Override
    public @NotNull Class<java.util.UUID> getArgumentClass() {
        return java.util.UUID.class;
    }
}
