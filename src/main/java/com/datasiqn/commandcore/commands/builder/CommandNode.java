package com.datasiqn.commandcore.commands.builder;

import com.datasiqn.commandcore.commands.builder.impl.CommandContextImpl;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class CommandNode<S extends CommandSender, This extends CommandNode<S, This>> implements Comparable<CommandNode<S, This>> {
    protected final Set<CommandNode<S, ?>> children = new HashSet<>();

    protected Consumer<CommandContext<S>> executor;

    public final @NotNull This then(CommandNode<S, ?> node) {
        children.add(node);
        return getThis();
    }

    public final @NotNull This executes(Consumer<CommandContext<S>> executor) {
        this.executor = executor;
        return getThis();
    }

    public final boolean executeWith(S sender, List<String> args) {
        if (executor == null) return false;
        executor.accept(new CommandContextImpl<>(sender, args));
        return true;
    }

    @NotNull
    public List<String> getTabComplete() {
        return new ArrayList<>();
    }

    @Contract(" -> new")
    public final @NotNull @Unmodifiable Set<CommandNode<S, ?>> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    public abstract boolean isApplicable(String arg);

    protected List<String> getUsages(boolean isOptional) {
        List<String> usages = new ArrayList<>();
        if (executor != null) usages.add(getUsageArgument(isOptional));
        boolean hasOptional = false;
        boolean canBeOptional = false;
        for (CommandNode<S, ?> node : children) {
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

    @Override
    public final int compareTo(@NotNull CommandNode<S, This> o) {
        return o.getPriority() - getPriority();
    }
}
