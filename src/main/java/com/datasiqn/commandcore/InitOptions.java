package com.datasiqn.commandcore;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Initialization options used when initializing {@code CommandCore}. Cannot be created directly, and must use a {@code Builder} to create one.
 * @see Builder
 */
public class InitOptions {
    private final String rootCommand;
    private final String pluginName;
    private final boolean helpCommand;
    private final List<String> aliases;
    private final Set<Warning> warnings;
    private final int commandsPerPage;

    /**
     * Creates an {@code InitOptions} from a {@code Builder}
     * @param builder The builder
     */
    @Contract(pure = true)
    public InitOptions(@NotNull Builder builder) {
        this.rootCommand = builder.rootCommand;
        this.helpCommand = builder.helpCommand;
        this.pluginName = builder.pluginName;
        this.aliases = Arrays.asList(builder.aliases);
        this.warnings = Arrays.stream(builder.warnings).collect(Collectors.toSet());
        this.commandsPerPage = builder.commandsPerPage;
    }

    /**
     * Gets the root command
     * @return The root command
     */
    public String getRootCommand() {
        return rootCommand;
    }

    /**
     * Gets whether the user has defined a custom plugin name or not
     * @return {@code true} if there is a custom plugin name, {@code false} otherwise
     */
    public boolean hasCustomPluginName() {
        return pluginName != null;
    }

    /**
     * Gets the custom plugin name
     * @return The custom plugin name, or null if there is none
     */
    public @Nullable String getPluginName() {
        return pluginName;
    }

    /**
     * Gets all the command's aliases
     * @return An unmodifiable view of all the command's aliases
     */
    @UnmodifiableView
    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
    }

    /**
     * Gets the commands per help page
     * @return The commands per help page
     */
    public int getCommandsPerPage() {
        return commandsPerPage;
    }

    /**
     * Gets whether it should warn the user on a specific warning or not
     * @param warning The warning to check for
     * @return {@code true} if it should warn, {@code false} otherwise
     */
    @UnmodifiableView
    public boolean shouldWarn(@NotNull Warning warning) {
        return warnings.contains(warning);
    }

    /**
     * Warns a specific warning if a condition is met and {@link #shouldWarn(Warning) shouldWarn(warning)} returns {@code true}
     * @param warning The warning to warn
     * @param condition The condition to meet in order to warn
     * @param args The args used to format the warning message
     */
    public void warnIf(@NotNull Warning warning, boolean condition, Object... args) {
        if (shouldWarn(warning) && condition) Bukkit.getLogger().warning("[CommandCore] " + String.format(warning.message, args));
    }

    /**
     * Gets whether a help command should be generated or not
     * @return {@code true} if a help command should be generated, {@code false} otherwise
     */
    public boolean createHelpCommand() {
        return helpCommand;
    }

    /**
     * Builder class to create an {@code InitOptions} object
     */
    public static class Builder {
        private final String rootCommand;
        private String pluginName;
        private boolean helpCommand = true;
        private String[] aliases = new String[0];
        private Warning[] warnings = new Warning[0];
        private int commandsPerPage = 5;

        /**
         * Creates a new {@code Builder} class
         * @param rootCommand The name of the root command that be the root all {@code CommandCore} commands
         */
        public Builder(@NotNull String rootCommand) {
            this.rootCommand = rootCommand;
        }

        /**
         * Sets whether a help command should be created or not
         * @param flag {@code true} if a help command should be created, {@code false} if it shouldn't
         * @return The builder, for chaining
         */
        public Builder createHelpCommand(boolean flag) {
            this.helpCommand = flag;
            return this;
        }

        /**
         * Sets the custom plugin name that appears when showing the help screen
         * @param name The custom plugin name
         * @return The builder, for chaining
         */
        public Builder pluginName(@NotNull String name) {
            this.pluginName = name;
            return this;
        }

        /**
         * Sets the aliases of the root command
         * @param aliases The aliases
         * @return The builder, for chaining
         */
        public Builder aliases(@NotNull String @NotNull ... aliases) {
            this.aliases = aliases;
            return this;
        }

        /**
         * Tells {@code CommandCore} what warnings it should give when you register a command
         * @param warnings The warnings
         * @return The builder, for chaining
         */
        public Builder warnOn(@NotNull Warning @NotNull ... warnings) {
            this.warnings = warnings;
            return this;
        }

        /**
         * Sets the number of commands per help page. Default is 5.
         * @param commandsPerPage The new commands per page
         * @return The builder, for chaining
         */
        public Builder commandsPerPage(int commandsPerPage) {
            this.commandsPerPage = commandsPerPage;
            return this;
        }

        /**
         * Creates a new {@code InitOptions} based off this builder
         * @return The newly created {@code InitOptions} instance
         */
        public InitOptions build() {
            return new InitOptions(this);
        }

        /**
         * Creates a new {@code Builder} instance. This is identical to calling {@code new Builder(rootCommand)}.
         * @param rootCommand The name of the root command that will be the root of all {@code CommandCore} commands
         * @return The newly created {@code Builder} instance
         */
        @Contract(value = "_ -> new", pure = true)
        public static @NotNull Builder create(@NotNull String rootCommand) {
            return new Builder(rootCommand);
        }
    }

    /**
     * A collection of possible errors when making command creation
     */
    public enum Warning {
        /**
         * The command is missing a description field. This has 1 format argument that is the command name.
         */
        MISSING_DESCRIPTION("Command %s is missing a description"),

        /**
         * The command is missing a permission. This has 1 format argument that is the command name.
         */
        MISSING_PERMISSION("Command %s is missing a permission"),
        ;

        private final String message;

        Warning(String message) {
            this.message = message;
        }

        /**
         * Gets the message of the warning. This should be used along with {@link String#format(String, Object...)} to get a correctly formatted message.
         * @return The message of the warning
         */
        public String getMessage() {
            return message;
        }
    }
}
