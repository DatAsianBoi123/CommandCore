package com.datasiqn.commandcore.command.builder;

import com.datasiqn.commandcore.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a builder that creates commands
 */
public class CommandBuilder extends CommandLink<CommandBuilder> {
    protected final String name;
    protected String permission;
    protected String description;
    protected String[] aliases = new String[0];

    /**
     * Creates a new {@code CommandBuilder}
     * @param name The name of the command
     */
    public CommandBuilder(@NotNull String name) {
        this.name = name;
    }

    /**
     * Sets the permission of the command
     * @param permission The permission
     * @return The builder, for chaining
     */
    public CommandBuilder permission(@Nullable String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Sets the description of the command
     * @param description The description
     * @return The builder, for chaining
     */
    public CommandBuilder description(@Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the command's aliases
     * @param aliases The aliases
     * @return The builder, for chaining
     */
    public CommandBuilder alias(@NotNull String @NotNull ... aliases) {
        this.aliases = aliases;
        return this;
    }

    /**
     * Creates a new {@link Command} instance using the supplied values
     * @return The built {@link Command} instance
     */
    public @NotNull Command build() {
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

        return new BuilderCommand(this, usages);
    }

    @Override
    protected @NotNull CommandBuilder getThis() {
        return this;
    }
}
