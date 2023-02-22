package com.datasiqn.commandcore.commands.builder.executor;

import com.datasiqn.commandcore.ArgumentParseException;
import com.datasiqn.commandcore.arguments.Arguments;
import com.datasiqn.commandcore.commands.CommandExecutor;
import com.datasiqn.commandcore.commands.builder.CommandNode;
import com.datasiqn.commandcore.commands.context.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class LegacyExecutor implements CommandExecutor {
    private final List<String> currentTabComplete = new ArrayList<>();
    private final Set<CommandNode<?>> nodes;
    private final Consumer<CommandContext> executor;
    private final List<Function<CommandContext, Result<None, String>>> requires;

    private Set<CommandNode<?>> currentNodes;
    private int lastSeenSize;

    public LegacyExecutor(Consumer<CommandContext> executor, Set<CommandNode<?>> nodes, List<Function<CommandContext, Result<None, String>>> requires) {
        this.currentNodes = nodes;
        this.nodes = nodes;
        this.executor = executor;
        this.requires = requires;
    }

    @Override
    public @NotNull Result<None, List<String>> execute(@NotNull CommandContext context) {
        long begin = System.currentTimeMillis();

        Arguments args = context.getArguments();

        if (args.size() >= 1) {
            if (nodes.isEmpty()) return Result.error(Collections.singletonList("Expected no parameters, but got " + args.size() + " parameters instead"));

            // If the user adds an extra space on the end of the command
            if (lastSeenSize > args.size()) {
                lastSeenSize = args.size();
                ParseResult result = findCurrentNode(nodes, args, 0, args.size() - 1);
                if (result == null || !result.foundNode()) currentNodes = nodes;
                else currentNodes = result.node.getChildren();
            }

            if (currentNodes == null) {
                String arg = args.getString(lastSeenSize - 1);
                return Result.error(Collections.singletonList("Expected end of input, but got '" + arg + "' at position " + lastSeenSize + " instead"));
            }

            ParseResult result = checkApplicable(args.getString(lastSeenSize - 1), currentNodes);
            if (!result.foundNode()) {
                String arg = args.getString(lastSeenSize - 1);
                List<String> messages = new ArrayList<>();
                messages.add("Invalid parameter '" + arg + "' at position " + lastSeenSize + ": ");
                result.exceptions.forEach(exception -> messages.add(exception.getMessage()));
                return Result.error(messages);
            }
            Bukkit.getLogger().info("[CommandCore] Command took " + (System.currentTimeMillis() - begin) + "ms");
            if (result.node.getExecutor() == null) return Result.error(Collections.emptyList());
            Result<None, String> executeResult = result.node.executeWith(context);
            if (executeResult.isError()) {
                context.getSource().getSender().sendMessage(ChatColor.RED + executeResult.unwrapError());
                return Result.ok();
            }
            return Result.ok();
        }

        if (executor == null) return Result.error(Collections.singletonList("Expected parameters, but got no parameters instead"));
        Bukkit.getLogger().info("[CommandCore] Command took " + (System.currentTimeMillis() - begin) + "ms");
        Result<None, String> requireResult = Result.ok();
        for (Function<CommandContext, Result<None, String>> require : requires) {
            requireResult = requireResult.and(require.apply(context));
        }
        if (requireResult.isError()) {
            context.getSource().getSender().sendMessage(ChatColor.RED + requireResult.unwrapError());
            return Result.ok();
        }
        executor.accept(context);
        return Result.ok();
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
                    currentNodes = result.node.getChildren();
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
            nodeSet = parseResult.node.getChildren();
            result = parseResult;
        }
        return result;
    }

    private static class ParseResult {
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
