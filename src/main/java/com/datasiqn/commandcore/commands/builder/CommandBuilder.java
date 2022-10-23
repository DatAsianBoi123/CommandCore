package com.datasiqn.commandcore.commands.builder;

import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.arguments.Arguments;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.CommandOutput;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class CommandBuilder<S extends CommandSender> {
    private final Set<CommandNode<S, ?>> nodes = new HashSet<>();
    private final Class<S> senderClass;

    private String permission;
    private Consumer<S> executor;
    private String description = "No description provided";

    public CommandBuilder(Class<S> senderClass) {
        this.senderClass = senderClass;
    }

    public CommandBuilder<S> permission(String permission) {
        this.permission = permission;
        return this;
    }

    public CommandBuilder<S> description(String description) {
        this.description = description;
        return this;
    }

    public CommandBuilder<S> then(CommandNode<S, ?> node) {
        nodes.add(node);
        return this;
    }

    public CommandBuilder<S> executes(Consumer<S> executor) {
        this.executor = executor;
        return this;
    }

    public Command build() {
        return new Command() {
            private final List<String> currentTabComplete = new ArrayList<>();
            private Set<CommandNode<S, ?>> currentNodes;
            private int argsSize;

            @Override
            public CommandOutput execute(@NotNull CommandSender sender, @NotNull Arguments args) {
                if (!senderClass.isInstance(sender)) {
                    sender.sendMessage("You cannot send this command");
                    return CommandOutput.success();
                }

                long begin = System.currentTimeMillis();
                S castedSender = senderClass.cast(sender);

                if (args.size() >= 1) {
                    if (nodes.isEmpty()) return CommandOutput.failure("Invalid argument size: expected 1 but got " + args.size());

                    Optional<CommandNode<S, ?>> optionalNode = checkAt(args, argsSize - 1, currentNodes);
                    if (currentNodes == null || !optionalNode.isPresent()) {

                        Optional<String> optionalArg = args.get(argsSize - 1, ArgumentType.STRING);
                        return optionalArg.map(s -> CommandOutput.failure("Invalid parameter '" + s + "' at position " + argsSize)).orElseGet(() -> CommandOutput.failure("Invalid parameter at position " + argsSize));
                    }
                    Optional<String> optionalArg = args.get(args.size() - 1, ArgumentType.STRING);
                    if (!optionalArg.isPresent()) return CommandOutput.failure("An unexpected error occurred");
                    boolean hasExecutor = optionalNode.get().executeWith(castedSender, args.asList());
                    Bukkit.getLogger().info("[CommandCore] Command took " + (System.currentTimeMillis() - begin) + "ms");
                    return hasExecutor ? CommandOutput.success() : CommandOutput.failure();
                }

                if (executor == null) return CommandOutput.failure("Invalid argument size: 1");
                Bukkit.getLogger().info("[CommandCore] Command took " + (System.currentTimeMillis() - begin) + "ms");
                executor.accept(castedSender);
                return CommandOutput.success();
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull Arguments args) {
                if (args.size() >= 1) {
                    if (argsSize != args.size()) {
                        argsSize = args.size();
                        FoundNode<S> foundNode = findCurrentNode(args, args.size() - 1);
                        if (args.size() == 1) {
                            currentNodes = nodes;
                            currentTabComplete.clear();
                            currentNodes.forEach(node -> currentTabComplete.addAll(node.getTabComplete()));
                        } else if (foundNode.node == null) {
                            currentNodes = null;
                            return Command.super.tabComplete(sender, args);
                        } else {
                            currentNodes = foundNode.node.getChildren();
                            currentTabComplete.clear();
                            currentNodes.forEach(node -> currentTabComplete.addAll(node.getTabComplete()));
                        }
                    }
                    return currentTabComplete;
                }
                return Command.super.tabComplete(sender, args);
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
            public List<String> getUsages() {
                List<String> usages = new ArrayList<>();
                if (executor != null) usages.add("");
                boolean hasOptional = false;
                boolean canBeOptional = false;
                for (CommandNode<S, ?> node : nodes) {
                    if (node.executor != null) hasOptional = true;
                    if (node.canBeOptional()) canBeOptional = true;
                    usages.addAll(node.getUsages(executor != null));
                }
                if (executor != null && hasOptional && canBeOptional) usages.remove(0);
                return usages;
            }
        };
    }

    private Optional<CommandNode<S, ?>> checkAt(@NotNull Arguments args, int index, Collection<CommandNode<S, ?>> nodes) {
        return args.get(index, ArgumentType.STRING).flatMap(s -> nodes.stream().filter(node -> node.isApplicable(s)).sorted().findFirst());
    }

    private @NotNull FoundNode<S> findCurrentNode(@NotNull Arguments args, int iterations) {
        Set<CommandNode<S, ?>> nodeSet = nodes;
        CommandNode<S, ?> currentNode = null;
        for (int i = 0; i < iterations; i++) {
            Optional<CommandNode<S, ?>> commandNode = checkAt(args, i, nodeSet);
            if (!commandNode.isPresent()) return new FoundNode<>(null);
            nodeSet = commandNode.get().getChildren();
            currentNode = commandNode.get();
        }
        return new FoundNode<>(currentNode);
    }

    private static class FoundNode<S extends CommandSender> {
        private final CommandNode<S, ?> node;

        public FoundNode(@Nullable CommandNode<S, ?> node) {
            this.node = node;
        }
    }
}
