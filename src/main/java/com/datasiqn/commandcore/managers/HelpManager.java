package com.datasiqn.commandcore.managers;

import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * @return The command names on that page. The size of this list will always be <= {@code commandsPerPage}.
     */
    @UnmodifiableView
    public List<String> getCommandNames(int page) {
        int startingIndex = commandsPerPage * (page - 1);
        if (startingIndex >= commandNames.size()) return Collections.emptyList();
        return Collections.unmodifiableList(commandNames.subList(startingIndex, Math.min(commandNames.size(), startingIndex + commandsPerPage)));
    }

    /**
     * Gets the number of commands per page
     * @return The commands per page
     */
    public int getCommandsPerPage() {
        return commandsPerPage;
    }
}
