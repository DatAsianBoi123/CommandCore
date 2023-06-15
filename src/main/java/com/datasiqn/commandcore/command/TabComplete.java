package com.datasiqn.commandcore.command;

import java.util.List;

/**
 * Represents the tab completions of a command
 */
public class TabComplete {
    private final List<String> values;
    private final String matchingString;

    /**
     * Creates a new {@code TabComplete}
     * @param values The tab complete values
     * @param matchingString The matching string used to filter {@code values}
     */
    public TabComplete(List<String> values, String matchingString) {
        this.values = values;
        this.matchingString = matchingString;
    }

    /**
     * Gets the tab complete values
     * @return The tab complete values
     */
    public List<String> values() {
        return values;
    }

    /**
     * Gets the string used to filter tab complete values
     * @return The matching string
     */
    public String getMatchingString() {
        return matchingString;
    }
}
