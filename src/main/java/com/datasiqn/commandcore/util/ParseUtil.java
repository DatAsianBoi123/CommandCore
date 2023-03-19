package com.datasiqn.commandcore.util;

import org.jetbrains.annotations.NotNull;

/**
 * A utility class that contains parsing methods
 */
public final class ParseUtil {
    private ParseUtil() {}

    /**
     * Strictly parses {@code str} as a boolean
     * @param str The string to parse
     * @return {@code true} if {@code str} equals "true", or {@code false} if {@code str} equals "false"
     * @throws IllegalArgumentException If {@code str} does not equal "true" or "false"
     */
    public static boolean strictParseBoolean(@NotNull String str) {
        if (str.equalsIgnoreCase("true")) return true;
        else if (str.equalsIgnoreCase("false")) return false;
        throw new IllegalArgumentException("String '" + str + "' is not a boolean");
    }
}
