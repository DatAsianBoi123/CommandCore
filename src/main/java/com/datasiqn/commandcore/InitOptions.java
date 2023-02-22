package com.datasiqn.commandcore;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class InitOptions {
    private final String rootCommand;
    private final String pluginName;
    private final boolean helpCommand;
    private final boolean legacyExecutor;

    @Contract(pure = true)
    public InitOptions(@NotNull Builder builder) {
        this.rootCommand = builder.rootCommand;
        this.pluginName = builder.pluginName;
        this.helpCommand = builder.helpCommand;
        this.legacyExecutor = builder.legacyExecutor;
    }

    public String getRootCommand() {
        return rootCommand;
    }

    public boolean hasCustomPluginName() {
        return pluginName != null;
    }

    public String getPluginName() {
        return pluginName;
    }

    public boolean createHelpCommand() {
        return helpCommand;
    }

    public boolean useLegacyExecutor() {
        return legacyExecutor;
    }

    public static class Builder {
        private final String rootCommand;
        private String pluginName;
        private boolean helpCommand = true;
        private boolean legacyExecutor = false;

        public Builder(String rootCommand) {
            this.rootCommand = rootCommand;
        }

        public Builder createHelpCommand(boolean flag) {
            this.helpCommand = flag;
            return this;
        }

        public Builder pluginName(String name) {
            this.pluginName = name;
            return this;
        }

        @Deprecated
        public Builder useLegacyExecutor() {
            this.legacyExecutor = true;
            return this;
        }

        public InitOptions build() {
            return new InitOptions(this);
        }

        @Contract(value = "_ -> new", pure = true)
        public static @NotNull Builder create(@NotNull String rootCommand) {
            return new Builder(rootCommand);
        }
    }
}
