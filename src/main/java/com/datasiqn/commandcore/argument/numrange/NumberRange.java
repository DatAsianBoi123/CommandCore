package com.datasiqn.commandcore.argument.numrange;

/**
 * Represents a range of numbers
 * @param <T> The type of the number
 */
public interface NumberRange<T extends Number & Comparable<T>> {
    /**
     * Gets whether {@code num} is within the bounds of this number range
     * @param num The number to test
     * @return {@code true} if {@code num} is within this range, otherwise {@code false}
     */
    boolean contains(T num);
}
