package com.datasiqn.commandcore.argument.type;

import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

class WordArgumentType implements SimpleArgumentType<String> {
    @Override
    public @NotNull String getTypeName() {
        return "";
    }

    @Override
    public @NotNull Result<String, None> parseWord(@NotNull String word) {
        return Result.ok(word);
    }
}
