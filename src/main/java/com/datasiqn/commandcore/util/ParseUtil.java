package com.datasiqn.commandcore.util;

import org.jetbrains.annotations.NotNull;

public final class ParseUtil {
    private ParseUtil() {}

    public static boolean strictParseBoolean(@NotNull String str) throws IllegalArgumentException {
        if (str.equalsIgnoreCase("true")) return true;
        else if (str.equalsIgnoreCase("false")) return false;
        throw new IllegalArgumentException("String '" + str + "' is not a boolean");
    }
}
