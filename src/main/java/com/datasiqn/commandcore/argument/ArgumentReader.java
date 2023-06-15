package com.datasiqn.commandcore.argument;

import org.jetbrains.annotations.NotNull;

/**
 * A class meant to read an argument. When used to parse an argument, make sure the reader is on the space after the argument.
 */
public interface ArgumentReader {
    /**
     * Gets the current character this reader is looking at
     * @return The current character
     */
    char get();

    /**
     * Advances the reader by 1, returning the current character after advancing
     *
     * <pre>
     *     {@code
     *
     *     char next = reader.next();
     *     char current = reader.get();
     *     assert next == current;
     *
     *     }
     * </pre>
     *
     * @throws IllegalStateException If the reader has already reached the last character
     * @return The current character after advancing 1 character
     */
    char next();

    /**
     * Gets whether the reader has reached the last character. If this is true, calling {@link #next()} will throw an exception.
     * This is the same as checking if {@link #index()} {@code ==} {@link #size()} {@code - 1}
     * @return {@code true} if the reader is at the last character, {@code false} otherwise
     */
    boolean atEnd();

    /**
     * Gets the current index of the reader
     * @return The current index of the reader
     */
    int index();

    /**
     * Gets the size of the underlying data
     * @return The size of the underlying data
     */
    int size();

    /**
     * Sets the index to something else, "jumping" to that location
     * @throws IndexOutOfBoundsException If {@code index} is negative or greater than or equal to {@link #size()}
     * @param index The index to set to
     */
    void jumpTo(int index);

    /**
     * Performs a splice that gets a string starting at index {@code beginning} and ending at index {@link #size()}
     *
     * <pre>
     *     {@code
     *
     *     // reader has the internal string of "hello there"
     *     String spliced = reader.splice(2);
     *     assert spliced.equals("llo there");
     *
     *     }
     * </pre>
     *
     * @param beginning The index to start at
     * @return The spliced string
     */
    @NotNull String splice(int beginning);

    /**
     * Performs a splice that gets a string starting at index {@code beginning} and ending at index {@code end}
     *
     * <pre>
     *     {@code
     *
     *     // reader has the internal string of "hello there"
     *     String spliced = reader.splice(2, 8);
     *     assert spliced.equals("llo th");
     *
     *     }
     * </pre>
     *
     * @param beginning The index to start at
     * @param end The index to end at
     * @return The spliced string
     */
    @NotNull String splice(int beginning, int end);

    /**
     * Returns the next word and places the reader on the space after that word, or the last character in the reader
     *
     * <pre>
     *     {@code
     *
     *     // reader has the internal string of "hello there"
     *     String word = reader.nextWord();
     *     assert word.equals("hello");
     *     assert reader.get() == ' ';
     *
     *     }
     * </pre>
     *
     * @return The next word
     */
    @NotNull String nextWord();
}
