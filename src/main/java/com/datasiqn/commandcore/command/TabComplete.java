package com.datasiqn.commandcore.command;

import java.util.List;

public class TabComplete {
    private final List<String> values;
    private final String matchingString;

    public TabComplete(List<String> values, String matchingString) {
        this.values = values;
        this.matchingString = matchingString;
    }

    public List<String> values() {
        return values;
    }

    public String getMatchingString() {
        return matchingString;
    }
}
