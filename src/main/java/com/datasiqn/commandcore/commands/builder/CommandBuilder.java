package com.datasiqn.commandcore.commands.builder;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.CommandExecutor;
import com.datasiqn.commandcore.commands.builder.executor.BuilderExecutor;
import com.datasiqn.commandcore.commands.builder.executor.LegacyExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a builder that creates commands
 */
public class CommandBuilder extends CommandLink<CommandBuilder> {
    private String permission;
    private String description = "No description provided";
    private String[] aliases;

    /**
     * Creates a new {@code CommandBuilder}
     */
    public CommandBuilder() {}

    /**
     * Sets the permission of the command
     * @param permission The permission
     * @return The builder, for chaining
     */
    public CommandBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Sets the description of the command
     * @param description The description
     * @return The builder, for chaining
     */
    public CommandBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CommandBuilder alias(String... aliases) {
        this.aliases = aliases;
        return this;
    }

    /**
     * Creates a new {@link Command} instance using the supplied values
     * @return The built {@link Command} instance
     */
    public Command build() {
        List<String> usages = new ArrayList<>();
        if (executor != null) usages.add("");
        boolean hasOptional = false;
        boolean canBeOptional = false;
        for (CommandNode<?> node : children) {
            if (node.executor != null) hasOptional = true;
            if (node.canBeOptional()) canBeOptional = true;
            usages.addAll(node.getUsages(executor != null));
        }
        if (executor != null && hasOptional && canBeOptional) usages.remove(0);

        return new BuilderCommand(usages);
    }

    @Override
    protected CommandBuilder getThis() {
        return this;
    }

    private class BuilderCommand implements Command {
        private final String description;
        private final String permission;
        private final List<String> usages;
        private final CommandExecutor commandExecutor;

        public BuilderCommand(List<String> usages) {
            this.description = CommandBuilder.this.description;
            this.permission = CommandBuilder.this.permission;
            this.usages = usages;

            this.commandExecutor = CommandCore.getInstance().getOptions().useLegacyExecutor() ? new LegacyExecutor(executor, children, requires) : new BuilderExecutor(executor, children, requires);
        }

        @Override
        public @NotNull CommandExecutor getExecutor() {
            return commandExecutor;
        }

        @Override
        public @Nullable String getPermissionString() {
            return permission;
        }

        @Override
        public @NotNull String getDescription() {
            return description;
        }

        @Override
        public @NotNull List<String> getUsages() {
            return usages;
        }

        @Override
        public @NotNull String[] getAliases() {
            return aliases == null ? new String[0] : aliases;
        }
    }
}
