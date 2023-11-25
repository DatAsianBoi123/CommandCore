package com.datasiqn.commandcore.argument.type;

import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

class FloatArgumentType implements SimpleArgumentType<Float> {
    @Override
    public @NotNull String getName() {
        return "float";
    }

    @Override
    public @NotNull Result<Float, None> parseWord(String word) {
        return Result.resolve(() -> Float.parseFloat(word));
    }

    @Override
    public @NotNull Class<Float> getArgumentClass() {
        return Float.class;
    }
}
