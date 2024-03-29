package com.datasiqn.commandcore.command.builder;

import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a link in a command tree.
 * It shares many common methods that are all present in a {@code CommandBuilder} and a {@code CommandNode}.
 * @param <T> The type of "This". It is returned every time a chaining method is called, allowing unique methods on the subclass to be called.
 */
public abstract class CommandLink<T> {
    protected final List<CommandNode<?>> children = new ArrayList<>();
    protected final List<Requirement> requires = new ArrayList<>();

    protected Executor executor;

    /**
     * Requires the context in which the command is executed in to pass the {@code requires} check
     * @param requirement A function that determines if a {@code CommandContext} can run the command
     * @return Itself, for chaining
     */
    public T requires(@NotNull Requirement requirement) {
        this.requires.add(requirement);
        return getThis();
    }

    /**
     * Requires the sender to be a {@code Player}
     * @see #requires(Requirement)
     * @return Itself, for chaining
     */
    public T requiresPlayer() {
        return requires(context -> context.getSource().getPlayerChecked().and(Result.ok()).or(Result.error("A player is required to run this")));
    }

    /**
     * Requires the sender to be an {@code Entity}
     * @see #requires(Requirement)
     * @return Itself, for chaining
     */
    public T requiresEntity() {
        return requires(context -> context.getSource().getEntityChecked().and(Result.ok()).or(Result.error("An entity is required to run this")));
    }

    /**
     * Requires the sender to be a {@code BlockCommandSender}
     * @see #requires(Requirement)
     * @return Itself, for chaining
     */
    public T requiresBlock() {
        return requires(context -> context.getSource().getBlockChecked().and(Result.ok()).or(Result.error("A block is required to run this")));
    }

    /**
     * Requires the sender to be locatable
     * @see #requires(Requirement)
     * @return Itself, for chaining
     */
    public T requiresLocatable() {
        return requires(context -> context.getSource().getLocatableChecked().and(Result.ok()).or(Result.error("A sender with a location is required to run this")));
    }

    /**
     * Adds a new node onto this command builder
     * @param node The node
     * @return The builder, for chaining
     */
    public T then(@NotNull CommandNode<?> node) {
        children.add(node);
        return getThis();
    }

    /**
     * Sets the executor for this command
     * @param executor The executor
     * @return The builder, for chaining
     */
    public T executes(@NotNull Executor executor) {
        this.executor = executor;
        return getThis();
    }

    /**
     * Gets all children nodes
     * @return An unmodifiable view of all children nodes
     */
    @UnmodifiableView
    public @NotNull List<CommandNode<?>> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Gets the executor
     * @return The executor
     */
    public Executor getExecutor() {
        return executor;
    }

    protected abstract @NotNull T getThis();

    /**
     * A function that defines a command executor
     */
    public interface Executor {
        /**
         * Executes the command
         * @param context The context in which the command was executed in
         */
        void execute(CommandContext context);
    }

    /**
     * A function that defines a command requirement.
     */
    public interface Requirement {
        /**
         * Tests this requirement
         * @param context The context in which the requirement is being tested in
         * @return A result, where the error value is an error message. An {@code Ok} value means the requirement passed, while an {@code Error} value means the requirement failed
         */
        Result<None, String> testRequirement(CommandContext context);
    }
}
