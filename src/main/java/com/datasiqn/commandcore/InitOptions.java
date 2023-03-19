package com.datasiqn.commandcore;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Initialization options used when initializing {@code CommandCore}. Cannot be created directly, and must use a {@code Builder} to create one.
 * @see Builder
 */
public class InitOptions {
    private final String rootCommand;
    private final String pluginName;
    private final boolean helpCommand;
    private final boolean legacyExecutor;
    private final List<String> aliases;

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
        this.legacyExecutor = builder.legacyExecutor;
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
     * @return True if there is a custom plugin name, false otherwise
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
     * Gets whether a help command should be generated or not
     * @return True if a help command should be generated, false otherwise
     */
    public boolean createHelpCommand() {
        return helpCommand;
    }

    /**
     * Gets whether the legacy executor should be used or not
     * @return True if the legacy executor should be used, false otherwise
     */
    public boolean useLegacyExecutor() {
        return legacyExecutor;
    }

    /**
     * Builder class to create an {@code InitOptions} object
     */
    public static class Builder {
        private final String rootCommand;
        private String pluginName;
        private boolean helpCommand = true;
        private boolean legacyExecutor = false;
        private String[] aliases = new String[0];

        /**
         * Creates a new {@code Builder} class
         * @param rootCommand The name of the root command that be the root all {@code CommandCore} commands
         */
        public Builder(String rootCommand) {
            this.rootCommand = rootCommand;
        }

        /**
         * Sets whether a help command should be created or not
         * @param flag True if a help command should be created, false if it shouldn't
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
        public Builder pluginName(String name) {
            this.pluginName = name;
            return this;
        }

        /**
         * Sets the aliases of the root command
         * @param aliases The aliases
         * @return The builder, for chaining
         */
        public Builder aliases(String... aliases) {
            this.aliases = aliases;
            return this;
        }

        /**
         * Tells {@code CommandCore} to use the legacy executor when executing a command
         * @deprecated You should not use this ever, since the legacy executor is very buggy and can cause lots of problems if used
         * @return The builder, for chaining
         */
        @Deprecated
        public Builder useLegacyExecutor() {
            this.legacyExecutor = true;
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
}
