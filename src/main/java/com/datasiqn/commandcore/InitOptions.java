package com.datasiqn.commandcore;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InitOptions {
    private final String rootCommand;
    private final String pluginName;
    private final boolean helpCommand;
    private final boolean legacyExecutor;
    private final List<String> aliases;

    @Contract(pure = true)
    public InitOptions(@NotNull Builder builder) {
        this.rootCommand = builder.rootCommand;
        this.helpCommand = builder.helpCommand;
        this.pluginName = builder.pluginName;
        this.aliases = Arrays.asList(builder.aliases);
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

    @UnmodifiableView
    public List<String> getAliases() {
        return Collections.unmodifiableList(aliases);
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
        private String[] aliases = new String[0];

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

        public Builder aliases(String... aliases) {
            this.aliases = aliases;
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
