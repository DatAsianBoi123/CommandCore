package com.datasiqn.commandcore.commands.builder.executor;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.arguments.ArgumentReader;
import com.datasiqn.commandcore.arguments.Arguments;
import com.datasiqn.commandcore.arguments.ListArguments;
import com.datasiqn.commandcore.commands.CommandExecutor;
import com.datasiqn.commandcore.commands.builder.CommandNode;
import com.datasiqn.commandcore.commands.context.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
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
        ArgumentReader reader = args.asReader();
        int size = args.size();

        if (size >= 1) {
            if (nodes.isEmpty()) return Result.error(Collections.singletonList("Expected no parameters, but got " + size + " parameters instead"));

            Result<NodeArgumentResult, List<String>> result = findCurrentNode(reader);
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
            CommandContext newContext = buildContext(context, result.unwrap());
            CommandNode<?> node = result.unwrap().getNode();
            if (node.getExecutor() == null) return Result.error(Collections.emptyList());
            Result<None, String> executeResult = node.executeWith(newContext);
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
        ArgumentReader reader = args.asReader();

        Set<CommandNode<?>> nodeSet = nodes;

        CommandContext newContext = context;
        if (args.size() >= 1) {
            if (args.size() != 1) {
                Result<NodeArgumentResult, List<String>> result = findCurrentNode(reader);
                if (result.isError()) {
                    return CommandExecutor.super.tabComplete(newContext);
                }
                newContext = buildContext(context, result.unwrap());
                nodeSet = result.unwrap().getNode().getChildren();
            }
            List<String> tabcomplete = new ArrayList<>();
            CommandContext finalNewContext = newContext;
            nodeSet.forEach(node -> tabcomplete.addAll(node.getTabComplete(finalNewContext)));
            return tabcomplete;
        }
        return CommandExecutor.super.tabComplete(newContext);
    }

    @Contract("_, _ -> new")
    private @NotNull CommandContext buildContext(@NotNull CommandContext context, @NotNull CurrentNode result) {
        return CommandCore.createContext(context.getSource(), context.getCommand(), context.getLabel(), new ListArguments(result.getTabcomplete()));
    }

    private @NotNull Result<NodeArgumentResult, List<String>> checkApplicable(@NotNull ArgumentReader reader, @NotNull Set<CommandNode<?>> nodes) {
        List<CommandNode<?>> options = new ArrayList<>();
        List<String> exceptions = new ArrayList<>();
        if (reader.index() != 0) reader.next();
        for (CommandNode<?> node : nodes) {
            node.parse(reader.copy()).match(o -> options.add(node), exceptions::add);
        }
        if (options.isEmpty()) return Result.error(exceptions);
        options.sort(CommandNode.getComparator());
        int beforeIndex = reader.index();
        options.get(0).parse(reader);
        int afterIndex = reader.index() + (reader.atEnd() ? 1 : 0);
        NodeArgumentResult nodeArgumentResult = new NodeArgumentResult(options.get(0), Collections.singletonList(reader.section(beforeIndex, afterIndex)));
        return Result.ok(nodeArgumentResult);
    }

    private Result<NodeArgumentResult, List<String>> findCurrentNode(@NotNull ArgumentReader reader) {
        Set<CommandNode<?>> nodeSet = nodes;
        List<String> args = new ArrayList<>();
        CommandNode<?> result = null;
        while (reader.index() + 1 < reader.size()) {
            Result<NodeArgumentResult, List<String>> parseResult = checkApplicable(reader, nodeSet);
            if (parseResult.isError()) return Result.error(parseResult.unwrapError());
            result = parseResult.unwrap().getNode();
            nodeSet = result.getChildren();
            args.addAll(parseResult.unwrap().tabcomplete);
            System.out.println("iterated, argument is " + parseResult.unwrap().tabcomplete + ". reader index at " + (reader.index() + 1) + "/" + reader.size());
        }
        System.out.println(String.join(",", args));
        return Result.ok(new NodeArgumentResult(result, args));
    }

    private static class NodeArgumentResult {
        private final CommandNode<?> node;
        private final List<String> tabcomplete;

        public NodeArgumentResult(CommandNode<?> node, List<String> tabcomplete) {
            this.node = node;
            this.tabcomplete = tabcomplete;
        }

        public CommandNode<?> getNode() {
            return node;
        }

        public List<String> getTabcomplete() {
            return tabcomplete;
        }
    }
}
