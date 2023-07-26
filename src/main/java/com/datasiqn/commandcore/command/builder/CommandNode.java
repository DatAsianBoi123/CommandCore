package com.datasiqn.commandcore.command.builder;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a node that can be added onto a command
 * @param <This> The type of the extended class
 */
public abstract class CommandNode<This extends CommandNode<This>> extends CommandLink<CommandNode<This>> {
    private static final Comparator<CommandNode<?>> COMPARATOR = Comparator.comparingInt(CommandNode::getPriority);

    /**
     * Executes this node
     * @param context The context in which the command was executed
     * @return The result of the execution
     * @throws IllegalStateException If there's no executor for this {@code CommandNode}
     */
    public final @NotNull Result<None, String> executeWith(CommandContext context) {
        if (executor == null) throw new IllegalStateException("This CommandNode has no executor");
        for (Requirement require : requires){
            Result<None, String> result = require.testRequirement(context);
            if (result.isError()) return result;
        }
        executor.execute(context);
        return Result.ok();
    }

    /**
     * Gets the tabcomplete for this node
     * @param context The context in which the tab complete is being requested
     * @return The tabcomplete
     */
    @NotNull
    public abstract List<String> getTabComplete(@NotNull CommandContext context);

    /**
     * Attempts to parse a string
     * @param reader The reader to parse
     * @return The result of the parsing
     */
    @NotNull
    public abstract Result<?, String> parse(ArgumentReader reader);

    protected List<String> getUsages(boolean isOptional) {
        List<String> usages = new ArrayList<>();
        if (executor != null) usages.add(getUsageArgument(isOptional));
        boolean hasOptional = false;
        boolean canBeOptional = false;
        List<CommandNode<?>> sortedChildren = children.stream().sorted(COMPARATOR).collect(Collectors.toList());
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

    /**
     * Gets the comparator for command nodes
     * @return The comparator
     */
    public static Comparator<CommandNode<?>> getComparator() {
        return COMPARATOR;
    }
}
