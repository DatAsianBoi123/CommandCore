package com.datasiqn.commandcore.commands.builder;

import com.datasiqn.commandcore.ArgumentParseException;
import com.datasiqn.commandcore.arguments.Arguments;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.CommandExecutor;
import com.datasiqn.commandcore.commands.CommandOutput;
import com.datasiqn.commandcore.commands.context.CommandContext;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a builder that creates commands
 */
public class CommandBuilder {
    private final Set<CommandNode<?>> nodes = new HashSet<>();

    private String permission;
    private Consumer<CommandContext> executor;
    private String description = "No description provided";

    /**
     * Creates a new {@code CommandBuilder}
     */
    public CommandBuilder() {}

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
        private final CommandExecutor commandExecutor = new BuilderExecutor();

        public BuilderCommand(List<String> usages) {
            this.description = CommandBuilder.this.description;
            this.permission = CommandBuilder.this.permission;
            this.usages = usages;
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

        private class BuilderExecutor implements CommandExecutor {
            private final List<String> currentTabComplete = new ArrayList<>();
            private Set<CommandNode<?>> currentNodes = nodes;
            private int lastSeenSize;

            @Override
            public @NotNull CommandOutput execute(@NotNull CommandContext context) {
                long begin = System.currentTimeMillis();

                Arguments args = context.getArguments();

                if (args.size() >= 1) {
                    if (nodes.isEmpty()) return CommandOutput.failure("Expected no parameters, but got " + args.size() + " parameters instead");

                    // If the user adds an extra space on the end of the command
                    if (lastSeenSize > args.size()) {
                        lastSeenSize = args.size();
                        ParseResult result = findCurrentNode(nodes, args, 0, args.size() - 1);
                        if (result == null || !result.foundNode()) currentNodes = nodes;
                        else currentNodes = result.node.children;
                    }

                    if (currentNodes == null) {
                        String arg = args.getString(lastSeenSize - 1);
                        return CommandOutput.failure("Expected end of input, but got '" + arg + "' at position " + lastSeenSize + " instead");
                    }

                    ParseResult result = checkApplicable(args.getString(lastSeenSize - 1), currentNodes);
                    if (!result.foundNode()) {
                        String arg = args.getString(lastSeenSize - 1);
                        String[] messages = new String[result.exceptions.size() + 1];
                        messages[0] = "Invalid parameter '" + arg + "' at position " + lastSeenSize + ": ";
                        int i = 1;
                        for (ArgumentParseException exception : result.exceptions) {
                            messages[i] = exception.getMessage();
                            i++;
                        }
                        return CommandOutput.failure(messages);
                    }
                    Bukkit.getLogger().info("[CommandCore] Command took " + (System.currentTimeMillis() - begin) + "ms");
                    boolean hasExecutor = result.node.executeWith(context);
                    return hasExecutor ? CommandOutput.success() : CommandOutput.failure();
                }

                if (executor == null) return CommandOutput.failure("Expected parameters, but got no parameters instead");
                Bukkit.getLogger().info("[CommandCore] Command took " + (System.currentTimeMillis() - begin) + "ms");
                executor.accept(context);
                return CommandOutput.success();
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandContext context) {
                Arguments args = context.getArguments();

                if (args.size() >= 1) {
                    if (args.size() == 1) {
                        currentNodes = nodes;
                        lastSeenSize = 1;
                        populateTabComplete(currentTabComplete, currentNodes, context);
                    }
                    boolean movedBack = lastSeenSize > args.size();
                    if (lastSeenSize != args.size()) {
                        if (args.size() != 1) {
                            if (!movedBack && currentNodes == null) return CommandExecutor.super.tabComplete(context);
                            ParseResult result = findCurrentNode(movedBack ? nodes : currentNodes, args, movedBack ? 0 : lastSeenSize - 1, args.size() - 1);
                            lastSeenSize = args.size();
                            if (result == null || !result.foundNode()) {
                                currentNodes = null;
                                currentTabComplete.clear();
                                return CommandExecutor.super.tabComplete(context);
                            }
                            currentNodes = result.node.children;
                        }
                        populateTabComplete(currentTabComplete, currentNodes, context);
                    }
                    return currentTabComplete;
                }
                return CommandExecutor.super.tabComplete(context);
            }

            @Contract(mutates = "param1")
            private void populateTabComplete(@NotNull List<String> currentTabComplete, @NotNull Set<CommandNode<?>> nodes, CommandContext context) {
                currentTabComplete.clear();
                nodes.forEach(node -> currentTabComplete.addAll(node.getTabComplete(context)));
            }

            private @NotNull ParseResult checkApplicable(@NotNull String argToCheck, @NotNull Collection<CommandNode<?>> nodes) {
                List<CommandNode<?>> options = new ArrayList<>();
                List<ArgumentParseException> exceptions = new ArrayList<>();
                for (CommandNode<?> node : nodes) {
                    node.parse(argToCheck).match(o -> options.add(node), exceptions::add);
                }
                if (options.isEmpty()) return new ParseResult(exceptions);
                options.sort(CommandNode.getComparator());
                return new ParseResult(options.get(0));
            }

            private ParseResult findCurrentNode(@NotNull Set<CommandNode<?>> nodeSet, @NotNull Arguments args, int begin, int iterations) {
                ParseResult result = null;
                for (int i = begin; i < iterations; i++) {
                    ParseResult parseResult = checkApplicable(args.getString(i), nodeSet);
                    if (!parseResult.foundNode()) return parseResult;
                    nodeSet = parseResult.node.children;
                    result = parseResult;
                }
                return result;
            }

            private class ParseResult {
                private final List<ArgumentParseException> exceptions = new ArrayList<>();
                private final CommandNode<?> node;

                private ParseResult(Collection<ArgumentParseException> exceptions) {
                    this((CommandNode<?>) null);
                    this.exceptions.addAll(exceptions);
                }
                private ParseResult(CommandNode<?> node) {
                    this.node = node;
                }

                public boolean foundNode() {
                    return node != null;
                }
            }
        }
    }
}
