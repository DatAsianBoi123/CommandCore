package com.datasiqn.commandcore.commands.builder;

import com.datasiqn.commandcore.ArgumentParseException;
import com.datasiqn.commandcore.commands.context.CommandContext;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a node that can be added onto a command
 * @param <This> The type of the extended class
 */
public abstract class CommandNode<This extends CommandNode<This>> {
    private static final Comparator<CommandNode<?>> comparator = Comparator.comparingInt(CommandNode::getPriority);

    protected final Set<CommandNode<?>> children = new HashSet<>();

    protected Consumer<CommandContext> executor;

    /**
     * Adds a new node onto this current node
     * @param node The node
     * @return The node for chaining
     */
    public final @NotNull This then(CommandNode<?> node) {
        children.add(node);
        return getThis();
    }

    /**
     * Sets the executor for this node
     * @param executor The executor
     * @return The node for chaining
     */
    public final @NotNull This executes(Consumer<CommandContext> executor) {
        this.executor = executor;
        return getThis();
    }

    /**
     * Executes this node
     * @param context The context in which the command was executed
     * @return True if it was successful, false otherwise
     */
    public final boolean executeWith(CommandContext context) {
        if (executor == null) return false;
        executor.accept(context);
        return true;
    }

    /**
     * Gets the tabcomplete for this node
     * @return The tabcomplete
     */
    @NotNull
    public List<String> getTabComplete(@NotNull CommandContext context) {
        return new ArrayList<>();
    }

    /**
     * Gets the children of this node
     * @return A view of this node's children
     */
    @Contract(" -> new")
    @UnmodifiableView
    public final @NotNull @Unmodifiable Set<CommandNode<?>> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    /**
     * Attempts to parse a string
     * @param arg The string to parse
     * @return The result of the parsing
     */
    @NotNull
    public abstract Result<?, ArgumentParseException> parse(String arg);

    protected List<String> getUsages(boolean isOptional) {
        List<String> usages = new ArrayList<>();
        if (executor != null) usages.add(getUsageArgument(isOptional));
        boolean hasOptional = false;
        boolean canBeOptional = false;
        List<CommandNode<?>> sortedChildren = children.stream().sorted(comparator).collect(Collectors.toList());
        for (CommandNode<?> node : sortedChildren) {
            if (node.executor != null) hasOptional = true;
            if (node.canBeOptional()) canBeOptional = true;
            usages.addAll(node.getUsages(executor != null).stream().map(str -> getUsageArgument(isOptional) + " " + str).collect(Collectors.toList()));
        }
        if (executor != null && hasOptional && canBeOptional) usages.remove(0);
        return usages;
    }

    protected int getPriority() {
        return 1;
    }

    protected abstract String getUsageArgument(boolean isOptional);

    protected boolean canBeOptional() {
        return false;
    }

    @NotNull
    protected abstract This getThis();

    /**
     * Gets the comparator for command nodes
     * @return The comparator
     */
    public static Comparator<CommandNode<?>> getComparator() {
        return comparator;
    }
}
