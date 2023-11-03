package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

class RangedIntArgumentType extends IntArgumentType {
    private final int min;
    private final int max;

    public RangedIntArgumentType(int min) {
        this(min, Integer.MAX_VALUE);
    }
    public RangedIntArgumentType(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public @NotNull Result<Integer, String> parse(@NotNull ArgumentReader reader) {
        return super.parse(reader)
                .andThen(num -> num < min ? Result.error("Integer must not be below " + min) : Result.ok(num))
                .andThen(num -> num > max ? Result.error("Integer must not be above " + max) : Result.ok(num));
    }
}
