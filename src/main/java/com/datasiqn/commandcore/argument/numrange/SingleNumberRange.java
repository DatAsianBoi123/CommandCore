package com.datasiqn.commandcore.argument.numrange;

/**
 * Represents a {@code NumberRange} that is just a single number. Only numbers that are equal to this number are contained in this range.
 * @param <T> The type of the number
 */
public class SingleNumberRange<T extends Number & Comparable<T>> implements NumberRange<T> {
    private final T num;

    /**
     * Creates a new {@code NumberRange} with the single matching number of {@code num}
     * @param num The number to match for
     */
    public SingleNumberRange(T num) {
        this.num = num;
    }

    @Override
    public boolean contains(T num) {
        return this.num.compareTo(num) == 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        SingleNumberRange<?> that = (SingleNumberRange<?>) object;

        return num.equals(that.num);
    }
}
