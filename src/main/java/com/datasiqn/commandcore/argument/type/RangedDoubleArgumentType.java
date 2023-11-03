package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

class RangedDoubleArgumentType extends DoubleArgumentType {
    private final double min;
    private final double max;

    public RangedDoubleArgumentType(double min) {
        this(min, Double.POSITIVE_INFINITY);
    }
    public RangedDoubleArgumentType(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public @NotNull Result<Double, String> parse(@NotNull ArgumentReader reader) {
        return super.parse(reader)
                .andThen(num -> num < min ? Result.error("Double must not be below " + min) : Result.ok(num))
                .andThen(num -> num > max ? Result.error("Double must not be above " + max) : Result.ok(num));
    }
}
