package com.datasiqn.commandcore.argument.duration;

/**
 * Represents a unit of time, from ticks to days. Leap seconds and similar additions are not accounted for in durations.
 * If you need any units bigger than days, please message me on Discord because I am genuinely curious why you would.
 */
public enum TimeUnit {
    /**
     * Ticks (1/20 of a second). Ticks have a symbol of "t".
     */
    TICKS(1, "t"),
    /**
     * Milliseconds. There are 0.02 ticks in each millisecond. Milliseconds have a symbol of "ms".
     */
    MILLIS(0.02, "ms"),
    /**
     * Seconds. There are 20 ticks in each second. Seconds have a symbol of "s".
     */
    SECONDS(20, "s"),
    /**
     * Minutes. There are 1,200 ticks in each minute. Minutes have a symbol of "min".
     */
    MINUTES(1_200, "min"),
    /**
     * Hours. There are 72,000 ticks in each hour. Hours have a symbol of "h".
     */
    HOURS(72_000, "h"),
    /**
     * Days. There are 1,728,000 ticks in each day. Days have a symbol of "d".
     */
    DAYS(1_728_000, "d"),
    ;

    private final double tickRatio;
    private final String symbol;

    TimeUnit(double tickRatio, String symbol) {
        this.tickRatio = tickRatio;
        this.symbol = symbol;
    }

    /**
     * Gets the tick ratio of this unit. For example, a tick ratio of 20 means that there are 20 ticks in 1 of this unit.
     * @return The ticks ratio of this unit
     */
    public double getTickRatio() {
        return tickRatio;
    }

    /**
     * Gets the symbol of this unit. For example, the symbol for a second is "s".
     * @return The symbol of this unit
     */
    public String getSymbol() {
        return symbol;
    }
}
