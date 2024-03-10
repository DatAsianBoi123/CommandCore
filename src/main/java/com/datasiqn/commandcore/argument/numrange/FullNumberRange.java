package com.datasiqn.commandcore.argument.numrange;

/**
 * Represents a {@code NumberRange} that includes all numbers
 * @param <T> The type of the number
 */
public class FullNumberRange<T extends Number & Comparable<T>> implements NumberRange<T> {
    @Override
    public boolean contains(T num) {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return this.getClass() == obj.getClass();
    }
}
