package com.datasiqn.commandcore.managers;

import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Class that manages help page(s)
 */
public class HelpManager {
    private final List<String> commandNames = new ArrayList<>();
    private final int commandsPerPage;

    /**
     * Creates a new {@code HelpManager} with a specific number of commands per page
     * @param commandsPerPage The commands per page. Must be >= 1.
     */
    public HelpManager(int commandsPerPage) {
        if (commandsPerPage <= 0) throw new IllegalArgumentException();
        this.commandsPerPage = commandsPerPage;
    }

    /**
     * Adds a command name
     * @param command The command name to add
     */
    public void addCommandName(String command) {
        commandNames.add(command);
    }

    /**
     * Gets the help page on a specific page number
     * @param page The page to get
     * @param filter The filter that all command names have to pass.
     *               When this filter returns true, that command name is included in the returned list, otherwise it is not included.
     * @return The help page
     * @throws IllegalArgumentException If {@code page} is {@literal <} 1 or {@literal >} the total number of pages
     */
    @UnmodifiableView
    public HelpPage getHelpPage(int page, Predicate<String> filter) {
        if (page < 1) throw new IllegalArgumentException("page cannot be <1");
        List<String> filteredNames = commandNames.stream().filter(filter).toList();
        int startingIndex = commandsPerPage * (page - 1);
        if (startingIndex >= filteredNames.size()) throw new IllegalArgumentException("page cannot exceed the total number of pages");
        List<String> names = Collections.unmodifiableList(filteredNames.subList(startingIndex, Math.min(filteredNames.size(), startingIndex + commandsPerPage)));
        return new HelpPage(names, page, (int) Math.ceil(filteredNames.size() / (double) commandsPerPage));
    }

    /**
     * Gets the number of commands per page
     * @return The commands per page
     */
    public int getCommandsPerPage() {
        return commandsPerPage;
    }

    /**
     * Record that represents just one page containing command names that is used when displaying help pages
     * @param names Command names on this page
     * @param page The page number the command names are on
     * @param totalPages The total number of pages of help
     */
    public record HelpPage(List<String> names, int page, int totalPages) { }
}
