package com.datasiqn.commandcore.argument.type;

import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

class DoubleArgumentType implements SimpleArgumentType<Double> {
    @Override
    public @NotNull String getTypeName() {
        return "double";
    }

    @Override
    public @NotNull Result<Double, None> parseWord(@NotNull String word) {
        return Result.resolve(() -> Double.parseDouble(word));
    }
}
