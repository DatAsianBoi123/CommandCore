package com.datasiqn.commandcore.command;

import java.util.List;

/**
 * Represents the tab completions of a command
 */
public record TabComplete(List<String> values, String matchingString) {
    /**
     * Creates a new {@code TabComplete}
     *
     * @param values The tab complete values
     * @param matchingString The matching string used to filter {@code values}
     */
    public TabComplete {
    }

    /**
     * Gets the tab complete values
     * @return The tab complete values
     */
    @Override
    public List<String> values() {
        return values;
    }

    /**
     * Gets the string used to filter tab complete values
     * @return The matching string
     */
    @Override
    public String matchingString() {
        return matchingString;
    }
}
