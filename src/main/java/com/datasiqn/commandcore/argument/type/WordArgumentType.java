package com.datasiqn.commandcore.argument.type;

import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

class WordArgumentType implements SimpleArgumentType<String> {
    @Override
    public @NotNull String getName() {
        return "word";
    }

    @Override
    public @NotNull Result<String, None> parseWord(@NotNull String word) {
        return Result.ok(word);
    }

    @Override
    public @NotNull Class<String> getArgumentClass() {
        return String.class;
    }
}
