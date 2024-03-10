package com.datasiqn.commandcore.argument;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A class meant to read an argument. When used to parse an argument, make sure the reader is on the space after the argument.
 */
public interface ArgumentReader {
    /**
     * The escape character that is used in {@link #readUntilEscaped(char...)}
     */
    char ESCAPE_CHAR = '\\';

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
     * @return The current character after advancing 1 character
     * @throws IllegalStateException If the reader has already reached the last character
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
     * @param index The index to set to
     * @throws IndexOutOfBoundsException If {@code index} is negative or greater than or equal to {@link #size()}
     */
    void jumpTo(int index);

    /**
     * Performs a substring that gets a string starting at index {@code beginning} and ending at index {@link #size()}.
     * The character at index {@code beginning} is included.
     *
     * <pre>
     *     {@code
     *
     *     // reader has the internal string of "hello there"
     *     String spliced = reader.substring(2);
     *     assert spliced.equals("llo there");
     *
     *     }
     * </pre>
     *
     * @param beginning The index to start at
     * @return The spliced string
     * @throws IndexOutOfBoundsException If {@code beginning} is negative
     */
    default @NotNull String substring(int beginning) {
        return substring(beginning, size());
    }
    /**
     * Performs a substring that gets a string starting at index {@code beginning} and ending at index {@code end}.
     * The character at index {@code beginning} is included, but the character at index {@code end} isn't.
     *
     * <pre>
     *     {@code
     *
     *     // reader has the internal string of "hello there"
     *     String spliced = reader.substring(2, 8);
     *     assert spliced.equals("llo th");
     *
     *     }
     * </pre>
     *
     * @param beginning The index to start at
     * @param end The index to end at
     * @return The spliced string
     * @throws IndexOutOfBoundsException If {@code end} is greater than {@link #size()}, {@code beginning} is negative, or {@code beginning} is greater than {@code end}
     */
    @NotNull String substring(int beginning, int end);

    /**
     * Reads until it encounters one of {@code chars}, then returns everything that it read, not including the character it encountered.
     * This will always place the reader either at one of {@code chars}, such that {@link #get()} returns one of {@code chars}, or at the last character in the reader.
     *
     * <pre>
     *     {@code
     *
     *     // reader has the internal string of "[item1,in list,very cool]"
     *     assert readUntil(',', ']').equals("item1");
     *     assert get() == ',';
     *     reader.next();
     *     assert readUntil(',', ']').equals("in list");
     *     reader.next();
     *     assert readUntil(',', ']').equals("very cool");
     *     assert get() == ']';
     *
     *     }
     * </pre>
     *
     * @param chars The characters that will mark the end of the read string
     * @return A string that contains all the characters until it encounters one of {@code chars}. This string will never include any characters in {@code chars}.
     */
    @NotNull String readUntil(char... chars);

    /**
     * Similar to {@link #readUntil(char...)}, except this method accounts for escape characters. The escape character is defined in {@link #ESCAPE_CHAR}.
     * <p>
     * Because this method accounts for escape characters, there is a possibility that a character inside of {@code chars} will be present in the final read string.
     *
     * <pre>
     *     {@code
     *
     *     // reader has the internal string of "Jim,Pluto\, then Bob,Joe \ Mary,Mike \\,John \,"
     *     assert reader.readUntilEscaped(',').equals(ReadUntilResult.found("Jim"));
     *     reader.next();
     *     assert reader.readUntilEscaped(',').equals(ReadUntilResult.found("Jim, then Bob"));
     *     reader.next();
     *     assert reader.readUntilEscaped(',').equals(ReadUntilResult.found("Joe \\ Mary"));
     *     reader.next();
     *     assert reader.readUntilEscaped(',').equals(ReadUntilResult.found("Mike \\");
     *     reader.next();
     *     assert reader.readUntilEscaped(',').equals(ReadUntilResult.notFound("John ,");
     *
     *     }
     * </pre>
     *
     * @param chars The characters that will mark the end of the read string
     * @return The result after reading
     */
    @NotNull ReadUntilResult readUntilEscaped(char... chars);

    /**
     * Returns the next word and places the reader on the space after that word, or the last character in the reader.
     * This is the same as calling {@code readUntil(' ')}.
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
     * @see #readUntil(char...)
     */
    default @NotNull String nextWord() {
        return readUntil(' ');
    }

    /**
     * Returns the rest of the reader, including the current character (the one returned by {@link #get()}).
     * This will place the reader on the last character in the reader.
     * @return The rest of the characters in this reader
     */
    default @NotNull String rest() {
        if (size() == 0) return "";
        String rest = substring(index());
        jumpTo(size() - 1);
        return rest;
    }

    /**
     * Represents the result after doing a {@link ArgumentReader#readUntilEscaped(char...)}
     */
    class ReadUntilResult {
        private final String read;
        private final boolean foundEnd;

        protected ReadUntilResult(String read, boolean foundEnd) {
            this.read = read;
            this.foundEnd = foundEnd;
        }

        /**
         * Gets the read string
         * @return The read string
         */
        public String getRead() {
            return read;
        }

        /**
         * Gets whether the reading found one of the ending characters or not
         * @return {@code true} if one of the ending characters was found, {@code false} otherwise
         */
        public boolean foundEnd() {
            return foundEnd;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            ReadUntilResult that = (ReadUntilResult) object;

            if (foundEnd != that.foundEnd) return false;
            return read.equals(that.read);
        }

        /**
         * Returns a new {@code ReadUntilResult} that found one of the ending characters and with a read string of {@code read}
         * @param read The string that was read. This should not include one of the ending characters.
         * @return The newly created {@code ReadUntilResult}
         */
        @Contract(value = "_ -> new", pure = true)
        public static @NotNull ReadUntilResult found(String read) {
            return new ReadUntilResult(read, true);
        }

        /**
         * Returns a new {@code ReadUntilResult} that did not find one of the ending characters and with a read string of {@code read}
         * @param read The string that was read
         * @return The newly created {@code ReadUntilResult}
         */
        @Contract(value = "_ -> new", pure = true)
        public static @NotNull ReadUntilResult notFound(String read) {
            return new ReadUntilResult(read, false);
        }
    }
}
