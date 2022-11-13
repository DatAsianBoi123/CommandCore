package com.datasiqn.commandcore.managers;

import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commandMap = new HashMap<>();

    /**
     * Registers a new command
     * @param name The command name
     * @param command The command
     */
    public void registerCommand(String name, CommandBuilder<?> command) {
        commandMap.put(name, command.build());
    }

    /**
     * Gets the command from its name
     * @param name The name of the command
     * @return The command, or null if it doesn't exist
     */
    public Command getCommand(String name) {
        return commandMap.get(name);
    }

    /**
     * Checks whether a command with that name exists or not
     * @param name The command name
     * @return True if the command exists, otherwise false
     */
    public boolean hasCommand(String name) {
        return commandMap.containsKey(name);
    }

    /**
     * Gets a view of all registered commands
     * @return All registered commands
     */
    @UnmodifiableView
    public Map<String, Command> allCommands() {
        return Collections.unmodifiableMap(commandMap);
    }
}
