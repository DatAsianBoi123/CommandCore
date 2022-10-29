package com.datasiqn.commandcore;

/**
 * Thrown to indicate an error when parsing an argument
 */
public class ArgumentParseException extends Exception {
    /**
     * Creates a new {@code ArgumentParseException}
     * @param message The error message
     */
    public ArgumentParseException(String message) {
        super(message);
    }
}
