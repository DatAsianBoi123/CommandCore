package com.datasiqn.commandcore.argument.numrange;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@code NumberRange} that only has an end bound. A range that goes <i>to</i> a number.
 * @param <T>
 */
public class ToNumberRange<T extends Number & Comparable<T>> implements NumberRange<T> {
    private final T end;

    /**
     * Creates a new {@code NumberRange} with an inclusive ending number of {@code end}
     * @param end The inclusive ending number
     */
    public ToNumberRange(T end) {
        this.end = end;
    }

    @Override
    public boolean contains(@NotNull T num) {
        return num.compareTo(end) <= 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ToNumberRange<?> that = (ToNumberRange<?>) object;

        return end.equals(that.end);
    }
}
