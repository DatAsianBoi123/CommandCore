package com.datasiqn.commandcore.argument.duration;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a positive duration of some time, measured in Minecraft ticks (1/20 of a second).
 * Creating a {@code Duration} can be done by using the static method {@link #from(double, TimeUnit)}
 */
public class Duration {
    private final long ticks;

    private Duration(long ticks) {
        this.ticks = ticks;
    }

    /**
     * Gets the duration in Minecraft ticks (1/20 of a second)
     * @return The exact ticks this duration lasts for
     */
    public long ticks() {
        return ticks;
    }

    /**
     * Gets the duration in as a unit of {@code timeUnit}. To get the exact duration as a {@code long} in ticks, use {@link #ticks()} instead.
     * @param timeUnit The time unit the returned duration will be in
     * @return The duration as a unit of {@code timeUnit}
     */
    public double as(@NotNull TimeUnit timeUnit) {
        return ticks / timeUnit.getTickRatio();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Duration duration = (Duration) o;
        return ticks == duration.ticks;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(ticks);
    }

    /**
     * Creates a new {@code Duration} with a duration of {@code duration} with a unit of {@code timeUnit}.
     * Note that duration will be truncated to be a whole number.
     * For example, 1ms cannot be expressed in a whole number of ticks. This will cause the duration to be 0 ticks, or no time at all.
     * @param duration The duration number to use
     * @param timeUnit The unit that {@code duration} is in
     * @return The newly created {@code Duration}
     * @throws IllegalArgumentException If the duration is negative
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Duration from(double duration, @NotNull TimeUnit timeUnit) {
        long ticks = (long) (duration * timeUnit.getTickRatio());
        if (ticks < 0) throw new IllegalArgumentException("duration cannot be negative");
        return new Duration(ticks);
    }
}
