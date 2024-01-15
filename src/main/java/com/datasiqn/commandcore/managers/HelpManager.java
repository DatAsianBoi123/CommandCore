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
     * Gets the command names on a certain page
     * @param page The page to get
     * @param filter The filter that all command names have to pass.
     *               When this filter returns true, that command name is included in the returned list, otherwise it is not included.
     * @return The command names on that page. The size of this list will always be {@literal <}= {@code commandsPerPage}.
     */
    @UnmodifiableView
    public List<String> getCommandNames(int page, Predicate<String> filter) {
        List<String> filteredNames = commandNames.stream().filter(filter).toList();
        int startingIndex = commandsPerPage * (page - 1);
        if (startingIndex >= filteredNames.size()) return Collections.emptyList();
        return Collections.unmodifiableList(filteredNames.subList(startingIndex, Math.min(filteredNames.size(), startingIndex + commandsPerPage)));
    }

    /**
     * Gets the number of commands per page
     * @return The commands per page
     */
    public int getCommandsPerPage() {
        return commandsPerPage;
    }
}
