package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

class BoundedNumberArgumentType<T extends Number & Comparable<T>> extends NumberArgumentType<T> {
    private final T min;
    private final T max;

    public BoundedNumberArgumentType(Class<T> numberClass, T min) {
        this(numberClass, min, null);
    }
    public BoundedNumberArgumentType(Class<T> numberClass, T min, T max) {
        super(numberClass);
        this.min = min;
        this.max = max;
    }

    @Override
    public @NotNull Result<T, String> parse(@NotNull ArgumentReader reader) {
        Result<T, String> result = super.parse(reader)
                .andThen(num -> num.compareTo(min) < 0 ? Result.error(getName() + " must not be below " + min) : Result.ok(num));
        if (max != null) {
            result = result.andThen(num -> num.compareTo(max) > 0 ? Result.error(getName() + " must not be above " + max) : Result.ok(num));
        }
        return result;
    }
}
