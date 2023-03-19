package com.datasiqn.commandcore.commands.builder.executor;

import com.datasiqn.commandcore.arguments.Arguments;
import com.datasiqn.commandcore.commands.CommandExecutor;
import com.datasiqn.commandcore.commands.builder.CommandNode;
import com.datasiqn.commandcore.commands.context.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class BuilderExecutor implements CommandExecutor {
    private final Set<CommandNode<?>> nodes;
    private final Consumer<CommandContext> executor;
    private final List<Function<CommandContext, Result<None, String>>> requires;

    public BuilderExecutor(Consumer<CommandContext> executor, Set<CommandNode<?>> nodes, List<Function<CommandContext, Result<None, String>>> requires) {
        this.nodes = nodes;
        this.executor = executor;
        this.requires = requires;
    }

    @Override
    public @NotNull Result<None, List<String>> execute(@NotNull CommandContext context) {
        long begin = System.currentTimeMillis();

        Arguments args = context.getArguments();
        int size = args.size();

        if (size >= 1) {
            if (nodes.isEmpty()) return Result.error(Collections.singletonList("Expected no parameters, but got " + size + " parameters instead"));

            Result<CommandNode<?>, List<String>> result = findCurrentNode(args, size);
            if (result.isError()) {
                List<String> exceptions = result.unwrapError();
                String arg = args.getString(args.size() - 1);
                if (exceptions.isEmpty()) {
                    return Result.error(Collections.singletonList("Expected end of input, but got '" + arg + "' at position " + size + " instead"));
                }
                List<String> messages = new ArrayList<>();
                messages.add("Invalid parameter '" + arg + "' at position " + size + ": ");
                messages.addAll(exceptions);
                return Result.error(messages);
            }
            Bukkit.getLogger().info("[CommandCore] Command took " + (System.currentTimeMillis() - begin) + "ms");
            if (result.unwrap().getExecutor() == null) return Result.error(Collections.emptyList());
            Result<None, String> executeResult = result.unwrap().executeWith(context);
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

        Set<CommandNode<?>> nodeSet = nodes;

        if (args.size() >= 1) {
            if (args.size() != 1) {
                Result<CommandNode<?>, List<String>> result = findCurrentNode(args, args.size() - 1);
                if (result == null || result.isError()) {
                    return CommandExecutor.super.tabComplete(context);
                }
                nodeSet = result.unwrap().getChildren();
            }
            List<String> tabcomplete = new ArrayList<>();
            nodeSet.forEach(node -> tabcomplete.addAll(node.getTabComplete(context)));
            return tabcomplete;
        }
        return CommandExecutor.super.tabComplete(context);
    }

    private @NotNull Result<CommandNode<?>, List<String>> checkApplicable(@NotNull String argToCheck, @NotNull Set<CommandNode<?>> nodes) {
        List<CommandNode<?>> options = new ArrayList<>();
        List<String> exceptions = new ArrayList<>();
        for (CommandNode<?> node : nodes) {
            node.parse(argToCheck).match(o -> options.add(node), exceptions::add);
        }
        if (options.isEmpty()) return Result.error(exceptions);
        options.sort(CommandNode.getComparator());
        return Result.ok(options.get(0));
    }

    private Result<CommandNode<?>, List<String>> findCurrentNode(@NotNull Arguments args, int size) {
        Set<CommandNode<?>> nodeSet = nodes;
        Result<CommandNode<?>, List<String>> result = null;
        for (int i = 0; i < size; i++) {
            Result<CommandNode<?>, List<String>> parseResult = checkApplicable(args.getString(i), nodeSet);
            if (parseResult.isError()) return parseResult;
            nodeSet = parseResult.unwrap().getChildren();
            result = parseResult;
        }
        return result;
    }
}
