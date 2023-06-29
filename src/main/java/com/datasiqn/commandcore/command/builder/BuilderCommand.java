package com.datasiqn.commandcore.command.builder;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.argument.ListArguments;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.commandcore.command.TabComplete;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

class BuilderCommand implements Command {
    private final String name;
    private final String[] aliases;
    private final String description;
    private final String permission;
    private final List<String> usages;

    private final Set<CommandNode<?>> nodes;
    private final Consumer<CommandContext> executor;
    private final List<CommandLink.Requirement> requires;

    public BuilderCommand(@NotNull CommandBuilder commandBuilder, List<String> usages) {
        this.name = commandBuilder.name;
        this.aliases = commandBuilder.aliases;
        this.description = commandBuilder.description;
        this.permission = commandBuilder.permission;
        this.usages = usages;
        this.nodes = commandBuilder.children;
        this.executor = commandBuilder.executor;
        this.requires = commandBuilder.requires;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String @NotNull [] getAliases() {
        return aliases;
    }

    @Override
    public @NotNull Result<None, List<String>> execute(@NotNull CommandContext context) {
        Arguments args = context.getArguments();
        ArgumentReader reader = args.asReader();
        int size = args.size();

        if (size >= 1) {
            if (nodes.isEmpty()) return Result.error(Collections.singletonList("Expected no parameters, but got parameters instead"));

            CurrentNode current = findCurrentNode(reader);
            Result<CommandNode<?>, List<String>> resultNode = current.node;
            if (resultNode.isError()) {
                if (current.extraInput) {
                    return Result.error(Collections.singletonList("Expected end of input, but got extra args instead"));
                }
                List<String> exceptions = resultNode.unwrapError();
                List<String> messages = new ArrayList<>();
                List<String> matches = current.args;
                messages.add("Invalid parameter '" + matches.get(matches.size() - 1) + "' at position " + matches.size() + ": ");
                messages.addAll(exceptions);
                return Result.error(messages);
            }
            CommandNode<?> node = resultNode.unwrap();
            CommandContext newContext = buildContext(context, current);
            if (node.getExecutor() == null) return Result.error(Collections.emptyList());
            Result<None, String> executeResult = node.executeWith(newContext);
            if (executeResult.isError()) {
                context.getSource().getSender().sendMessage(ChatColor.RED + executeResult.unwrapError());
                return Result.ok();
            }
            return Result.ok();
        }

        if (executor == null) return Result.error(Collections.singletonList("Expected parameters, but got no parameters instead"));
        Result<None, String> requireResult = requires.stream().map(requirement -> requirement.apply(context)).reduce(Result.ok(), Result::and);
        if (requireResult.isError()) {
            context.getSource().getSender().sendMessage(ChatColor.RED + requireResult.unwrapError());
            return Result.ok();
        }
        executor.accept(context);
        return Result.ok();
    }

    @Override
    public @NotNull TabComplete tabComplete(@NotNull CommandContext context) {
        Arguments args = context.getArguments();

        if (args.size() >= 1) {
            ArgumentReader reader = args.asReader();
            CommandContext newContext = context;
            Set<CommandNode<?>> nodeSet = nodes;

            String matchingString = args.getString(args.size() - 1);

            if (args.size() != 1) {
                CurrentNode current = findCurrentNode(reader);
                List<CommandNode<?>> nodeList = current.nodes;
                if (nodeList.size() != 0) {
                    CommandNode<?> node = nodeList.get(nodeList.size() - 1);
                    newContext = buildContext(context, current);
                    nodeSet = node.getChildren();
                }
                matchingString = current.args.get(current.args.size() - 1);
            }
            List<String> tabcomplete = new ArrayList<>();
            for (CommandNode<?> node : nodeSet) {
                tabcomplete.addAll(node.getTabComplete(newContext));
            }
            return new TabComplete(tabcomplete, matchingString);
        }
        return Command.super.tabComplete(context);
    }

    @Override
    public @Nullable String getPermissionString() {
        return permission;
    }

    @Override
    public boolean hasPermission() {
        return permission != null;
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public boolean hasDescription() {
        return description != null;
    }

    @Override
    public @NotNull List<String> getUsages() {
        return usages;
    }

    @Contract("_, _ -> new")
    private @NotNull CommandContext buildContext(@NotNull CommandContext context, @NotNull CurrentNode result) {
        return CommandCore.createContext(context.getSource(), context.getCommand(), context.getLabel(), new ListArguments(result.args));
    }

    private @NotNull Result<ApplicableNode, List<String>> checkApplicable(@NotNull ArgumentReader reader, @NotNull Set<CommandNode<?>> nodes) {
        List<CommandNode<?>> options = new ArrayList<>();
        List<String> exceptions = new ArrayList<>();
        if (reader.index() != 0 && !reader.atEnd()) reader.next();
        int beforeIndex = reader.index();
        for (CommandNode<?> node : nodes) {
            node.parse(reader).match(val -> options.add(node), e -> {
                if (!e.isEmpty()) exceptions.add(e);
            });
            reader.jumpTo(beforeIndex);
        }
        if (options.isEmpty()) return Result.error(exceptions);
        options.sort(CommandNode.getComparator());
        options.get(0).parse(reader);
        String arg;
        if (reader.atEnd()) arg = reader.splice(beforeIndex);
        else arg = reader.splice(beforeIndex, reader.index());
        return Result.ok(new ApplicableNode(options.get(0), arg));
    }

    @Contract("_ -> new")
    private @NotNull CurrentNode findCurrentNode(@NotNull ArgumentReader reader) {
        Set<CommandNode<?>> nodeSet = nodes;
        List<String> args = new ArrayList<>();
        List<CommandNode<?>> nodeList = new ArrayList<>();
        CommandNode<?> node;
        do {
            if (nodeSet.isEmpty())
                return new CurrentNode(Result.error(Collections.emptyList()), nodeList, args, true);
            Result<ApplicableNode, List<String>> parseResult = checkApplicable(reader, nodeSet);
            if (parseResult.isError()) {
                args.add(reader.splice(reader.index()));
                return new CurrentNode(Result.error(parseResult.unwrapError()), nodeList, args, false);
            }
            ApplicableNode applicableNode = parseResult.unwrap();
            node = applicableNode.node;
            nodeSet = node.getChildren();
            nodeList.add(node);
            args.add(applicableNode.argument);

            if (reader.atEnd() && reader.get() == ' ') args.add("");
        } while (!reader.atEnd());
        return new CurrentNode(Result.ok(node), nodeList, args, false);
    }

    private static class ApplicableNode {
        private final CommandNode<?> node;
        private final String argument;

        private ApplicableNode(CommandNode<?> node, String argument) {
            this.node = node;
            this.argument = argument;
        }
    }

    private static class CurrentNode {
        private final Result<CommandNode<?>, List<String>> node;
        private final List<CommandNode<?>> nodes;
        private final List<String> args;
        private final boolean extraInput;

        public CurrentNode(Result<CommandNode<?>, List<String>> node, List<CommandNode<?>> nodes, List<String> args, boolean extraInput) {
            this.node = node;
            this.nodes = nodes;
            this.args = args;
            this.extraInput = extraInput;
        }
    }
}
