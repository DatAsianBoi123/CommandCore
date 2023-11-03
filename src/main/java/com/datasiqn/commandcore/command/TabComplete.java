package com.datasiqn.commandcore.command;

import java.util.List;

/**
 * Represents the tab completions of a command
 */
public record TabComplete(List<String> values, String matchingString) {
    /**
     * Creates a new {@code TabComplete}
     * @param values The tab complete values
     * @param matchingString The matching string used to filter {@code values}
     */
    public TabComplete {
    }
}
