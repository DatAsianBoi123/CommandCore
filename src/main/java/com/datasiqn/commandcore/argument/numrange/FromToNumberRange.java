package com.datasiqn.commandcore.argument.numrange;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@code NumberRange} that has both a start and an end bound. A range that goes <i>from</i>, <i>to</i> a number.
 * @param <T> The type of the number
 */
public class FromToNumberRange<T extends Number & Comparable<T>> implements NumberRange<T> {
    private final T start;
    private final T end;

    /**
     * Creates a new {@code NumberRange} with an inclusive starting number of {@code start} and an inclusive ending number of {@code end}
     * @param start The inclusive starting number
     * @param end The inclusive ending number
     */
    public FromToNumberRange(T start, T end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean contains(@NotNull T num) {
        return num.compareTo(start) >= 0 && num.compareTo(end) <= 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        FromToNumberRange<?> that = (FromToNumberRange<?>) object;

        if (!start.equals(that.start)) return false;
        return end.equals(that.end);
    }
}
