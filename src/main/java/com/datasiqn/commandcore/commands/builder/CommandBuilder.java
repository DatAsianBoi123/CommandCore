package com.datasiqn.commandcore.commands.builder;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.CommandExecutor;
import com.datasiqn.commandcore.commands.builder.executor.BuilderExecutor;
import com.datasiqn.commandcore.commands.builder.executor.LegacyExecutor;
import com.datasiqn.commandcore.commands.context.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a builder that creates commands
 */
public class CommandBuilder {
    private final Set<CommandNode<?>> nodes = new HashSet<>();
    private final List<Function<CommandContext, Result<None, String>>> requires = new ArrayList<>();

    private String permission;
    private Consumer<CommandContext> executor;
    private String description = "No description provided";
    private String[] aliases;

    /**
     * Creates a new {@code CommandBuilder}
     */
    public CommandBuilder() {}

    public CommandBuilder requires(Function<CommandContext, Result<None, String>> requires) {
        this.requires.add(requires);
        return this;
    }

    public CommandBuilder requiresPlayer() {
        return requires(context -> context.getSource().getPlayer().and(Result.ok()).or(Result.error("A player is required to run this")));
    }

    public CommandBuilder requiresEntity() {
        return requires(context -> context.getSource().getEntity().and(Result.ok()).or(Result.error("An entity is required to run this")));
    }

    /**
     * Sets the permission of the command
     * @param permission The permission
     * @return The builder for chaining
     */
    public CommandBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Sets the description of the command
     * @param description The description
     * @return The builder for chaining
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
     * Adds a new node onto this command builder
     * @param node The node
     * @return The builder for chaining
     */
    public CommandBuilder then(CommandNode<?> node) {
        nodes.add(node);
        return this;
    }

    /**
     * Sets the executor for this command
     * @param executor The executor
     * @return The builder for chaining
     */
    public CommandBuilder executes(Consumer<CommandContext> executor) {
        this.executor = executor;
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
        for (CommandNode<?> node : nodes) {
            if (node.executor != null) hasOptional = true;
            if (node.canBeOptional()) canBeOptional = true;
            usages.addAll(node.getUsages(executor != null));
        }
        if (executor != null && hasOptional && canBeOptional) usages.remove(0);

        return new BuilderCommand(usages);
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

            this.commandExecutor = CommandCore.getInstance().getOptions().useLegacyExecutor() ? new LegacyExecutor(executor, nodes, requires) : new BuilderExecutor(executor, nodes, requires);
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
