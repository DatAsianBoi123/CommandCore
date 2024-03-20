package com.datasiqn.commandcore.managers;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.InitOptions;
import com.datasiqn.commandcore.InitOptions.Warning;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.annotation.AnnotationCommand;
import com.datasiqn.commandcore.command.annotation.CommandBuilderGenerator;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import org.bukkit.Bukkit;
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
     * Registers an {@code AnnotationCommand} command.
     * <p>
     * Behind the scenes, this method uses {@link CommandBuilderGenerator#fromAnnotationCommand(AnnotationCommand)} to generate a {@link CommandBuilder} from an {@link AnnotationCommand}.
     * <p>
     * Errors can occur when parsing {@code command}. Any errors encountered will be logged with the {@code [CommandCore]} prefix, and registration will be aborted.
     * @param command The command to register
     * @throws IllegalArgumentException If {@code command}'s name or one of its aliases is empty or contains spaces.
     * If {@code command}'s name or one of its aliases are already used
     */
    public void registerCommand(@NotNull AnnotationCommand command) {
        CommandBuilderGenerator.fromAnnotationCommand(command).match(
                this::registerCommand,
                err -> Bukkit.getLogger().severe("[CommandCore] " + err)
        );
    }
    /**
     * Registers a {@code CommandBuilder} command
     * @param command The command to register
     * @throws IllegalArgumentException If {@code command}'s name or one of its aliases is empty or contains spaces.
     * If {@code command}'s name or one of its aliases are already used
     */
    public void registerCommand(@NotNull CommandBuilder command) {
        registerCommand(command.build());
    }
    private void registerCommand(@NotNull Command command) {
        String name = command.getName();
        if (name.contains(" ")) throw new IllegalArgumentException("Command name cannot contain spaces");
        if (name.isEmpty()) throw new IllegalArgumentException("Command name cannot be empty");
        InitOptions options = CommandCore.getInstance().getOptions();
        // the default help command doesn't have a permission, so suppress all warnings if the command is the default help command
        if (!options.createHelpCommand() || !command.getName().equals("help")) {
            options.warnIf(Warning.MISSING_DESCRIPTION, !command.hasDescription(), name);
            options.warnIf(Warning.MISSING_PERMISSION, !command.hasPermission(), name);
        }
        if (commandMap.putIfAbsent(name, command) != null) throw new IllegalArgumentException("Command name already in use");
        CommandCore.getInstance().getHelpManager().addCommandName(name);
        for (String alias : command.getAliases()) {
            if (alias.contains(" ")) throw new IllegalArgumentException("Command aliases cannot contain spaces");
            if (alias.isEmpty()) throw new IllegalArgumentException("Command aliases cannot be empty");
            Command prev = aliasesMap.putIfAbsent(alias, command);
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
