package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

class RangedFloatArgumentType extends FloatArgumentType {
    private final float min;
    private final float max;

    RangedFloatArgumentType(float min) {
        this(min, Float.POSITIVE_INFINITY);
    }
    RangedFloatArgumentType(float min, float max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public @NotNull Result<Float, String> parse(@NotNull ArgumentReader reader) {
        return super.parse(reader)
                .andThen(num -> num < min ? Result.error("Float value must not be below " + min) : Result.ok(num))
                .andThen(num -> num > max ? Result.error("Float value must not be above " + max) : Result.ok(num));
    }
}
