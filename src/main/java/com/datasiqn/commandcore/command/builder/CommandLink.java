package com.datasiqn.commandcore.command.builder;

import com.datasiqn.commandcore.command.context.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a link in a command tree.
 * It shares many common methods that are all present in a {@code CommandBuilder} and a {@code CommandNode}.
 * @param <T> The type of "This". It is returned every time a chaining method is called, allowing unique methods on the subclass to be called.
 */
public abstract class CommandLink<T> {
    protected final Set<CommandNode<?>> children = new HashSet<>();
    protected final List<Function<CommandContext, Result<None, String>>> requires = new ArrayList<>();

    protected Consumer<CommandContext> executor;

    /**
     * Requires the context in which the command is executed in to pass the {@code requires} check
     * @param requires A function that determines if a {@code CommandContext} can run the command
     * @return Itself, for chaining
     */
    public T requires(Function<CommandContext, Result<None, String>> requires) {
        this.requires.add(requires);
        return getThis();
    }

    /**
     * Requires the sender to be a {@code Player}
     * @see #requires(Function)
     * @return Itself, for chaining
     */
    public T requiresPlayer() {
        return requires(context -> context.getSource().getPlayer().and(Result.ok()).or(Result.error("A player is required to run this")));
    }

    /**
     * Requires the sender to be an {@code Entity}
     * @see #requires(Function)
     * @return Itself, for chaining
     */
    public T requiresEntity() {
        return requires(context -> context.getSource().getEntity().and(Result.ok()).or(Result.error("An entity is required to run this")));
    }

    /**
     * Adds a new node onto this command builder
     * @param node The node
     * @return The builder, for chaining
     */
    public T then(CommandNode<?> node) {
        children.add(node);
        return getThis();
    }

    /**
     * Sets the executor for this command
     * @param executor The executor
     * @return The builder, for chaining
     */
    public T executes(Consumer<CommandContext> executor) {
        this.executor = executor;
        return getThis();
    }

    /**
     * Gets the executor
     * @return the executor
     */
    public Consumer<CommandContext> getExecutor() {
        return executor;
    }

    protected abstract T getThis();
}
