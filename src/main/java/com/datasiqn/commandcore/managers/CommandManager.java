package com.datasiqn.commandcore.managers;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.InitOptions;
import com.datasiqn.commandcore.InitOptions.Warning;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that manages all commands
 */
public class CommandManager {
    private final Map<String, Command> commandMap = new HashMap<>();
    private final Map<String, Command> aliasesMap = new HashMap<>();

    /**
     * Registers a new command
     * @param command The command
     * @throws IllegalArgumentException If {@code command}'s name or one of its aliases is empty or contains spaces.
     * If {@code command}'s name or one of its aliases are already used
     */
    public void registerCommand(@NotNull CommandBuilder command) {
        Command builtCommand = command.build();
        String name = builtCommand.getName();
        if (name.contains(" ")) throw new IllegalArgumentException("Command name cannot contain spaces");
        if (name.isEmpty()) throw new IllegalArgumentException("Command name cannot be empty");
        InitOptions options = CommandCore.getInstance().getOptions();
        // the default help command doesn't have a permission, so suppress all warnings if the command is the default help command
        if (!options.createHelpCommand() || !builtCommand.getName().equals("help")) {
            options.warnIf(Warning.MISSING_DESCRIPTION, !builtCommand.hasDescription(), name);
            options.warnIf(Warning.MISSING_PERMISSION, !builtCommand.hasPermission(), name);
        }
        if (commandMap.putIfAbsent(name, builtCommand) != null) throw new IllegalArgumentException("Command name already in use");
        for (String alias : builtCommand.getAliases()) {
            if (alias.contains(" ")) throw new IllegalArgumentException("Command aliases cannot contain spaces");
            if (alias.isEmpty()) throw new IllegalArgumentException("Command aliases cannot be empty");
            Command prev = aliasesMap.putIfAbsent(alias, builtCommand);
            if (prev != null) throw new IllegalArgumentException("Command alias already in use (used by " + prev.getName() + ")");
        }
    }

    /**
     * Gets the command from its name
     * @param name The name of the command
     * @param alias Whether {@code name} is a command alias or not
     * @return The command, or null if it doesn't exist
     */
    public Command getCommand(String name, boolean alias) {
        return alias ? aliasesMap.get(name) : commandMap.get(name);
    }

    /**
     * Checks whether a command with that name exists or not
     * @param name The command name
     * @param alias Whether {@code name} is a command alias or not
     * @return {@code true} if the command exists, otherwise {@code false}
     */
    public boolean hasCommand(String name, boolean alias) {
        return alias ? aliasesMap.containsKey(name) : commandMap.containsKey(name);
    }

    /**
     * Gets whether {@code name} is a command alias or not
     * @param name The command name/alias
     * @return {@code true} if {@code name} is a command alias, {@code false} otherwise
     */
    public boolean isAlias(String name) {
        return aliasesMap.containsKey(name);
    }

    /**
     * Gets all command names
     * @param includeAliases Whether to include command aliases or not
     * @return All command names
     */
    public @NotNull Set<String> getCommandNames(boolean includeAliases) {
        Set<String> names = new HashSet<>(commandMap.keySet());
        if (includeAliases) names.addAll(aliasesMap.keySet());
        return names;
    }
}
