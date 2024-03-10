package com.datasiqn.commandcore.argument.numrange;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@code NumberRange} that only has a start bound. A range that goes <i>from</i> a number.
 * @param <T> The type of the number
 */
public class FromNumberRange<T extends Number & Comparable<T>> implements NumberRange<T> {
    private final T start;

    /**
     * Creates a new {@code NumberRange} with an inclusive starting number of {@code start}
     * @param start The inclusive starting number
     */
    public FromNumberRange(T start) {
        this.start = start;
    }

    @Override
    public boolean contains(@NotNull T num) {
        return num.compareTo(start) >= 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        FromNumberRange<?> that = (FromNumberRange<?>) object;

        return start.equals(that.start);
    }
}
