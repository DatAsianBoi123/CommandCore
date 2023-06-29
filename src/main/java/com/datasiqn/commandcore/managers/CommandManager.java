package com.datasiqn.commandcore.managers;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.InitOptions;
import com.datasiqn.commandcore.InitOptions.Warning;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that manages all commands
 */
public class CommandManager {
    private final Map<String, Command> executableCommands = new HashMap<>();
    private final Map<String, Command> commandMap = new HashMap<>();

    /**
     * Registers a new command
     * @param command The command
     * @throws IllegalArgumentException If {@code command}'s name or one of {@code command}'s aliases is empty or contains spaces
     */
    public void registerCommand(@NotNull CommandBuilder command) {
        Command builtCommand = command.build();
        String name = builtCommand.getName();
        if (name.contains(" ")) throw new IllegalArgumentException("Command name cannot contain spaces");
        if (name.isEmpty()) throw new IllegalArgumentException("Command name cannot be empty");
        InitOptions options = CommandCore.getInstance().getOptions();
        options.warnIf(Warning.MISSING_DESCRIPTION, !builtCommand.hasDescription(), name);
        options.warnIf(Warning.MISSING_PERMISSION, !builtCommand.hasPermission(), name);
        commandMap.put(name, builtCommand);
        executableCommands.put(name, builtCommand);
        for (String alias : builtCommand.getAliases()) {
            if (alias.contains(" ")) throw new IllegalArgumentException("Command aliases cannot contain spaces");
            if (alias.isEmpty()) throw new IllegalArgumentException("Command aliases cannot be empty");
            executableCommands.put(alias, builtCommand);
        }
    }

    /**
     * Gets the command from its name
     * @param name The name of the command
     * @return The command, or null if it doesn't exist
     */
    public Command getCommand(String name) {
        return executableCommands.get(name);
    }

    /**
     * Checks whether a command with that name exists or not
     * @param name The command name
     * @return {@code true} if the command exists, otherwise {@code false}
     */
    public boolean hasCommand(String name) {
        return executableCommands.containsKey(name);
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
