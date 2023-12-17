package com.datasiqn.commandcore.command.builder;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.argument.ParsedArguments;
import com.datasiqn.commandcore.argument.ParsedArguments.ParsedArgument;
import com.datasiqn.commandcore.argument.StringArguments;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.commandcore.command.TabComplete;
import com.datasiqn.commandcore.command.builder.CommandLink.Executor;
import com.datasiqn.commandcore.command.builder.CommandLink.Requirement;
import com.datasiqn.commandcore.command.source.CommandSource;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BuilderCommand implements Command {
    private final String name;
    private final String[] aliases;
    private final String description;
    private final String permission;
    private final List<String> usages;

    private final List<CommandNode<?>> nodes;
    private final Executor executor;
    private final List<Requirement> requires;

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
        CommandSource source = context.source();
        Arguments args = context.arguments();
        ArgumentReader reader = args.asReader();

        if (args.size() >= 1) {
            if (nodes.isEmpty()) return Result.error(Collections.singletonList("Expected no parameters, but got parameters instead"));

            CurrentNode current = findCurrentNode(reader);
            Result<CommandNode<?>, List<String>> resultNode = current.node;
            if (resultNode.isError()) {
                if (current.extraInput) {
                    return Result.error(Collections.singletonList("Expected end of input, but got extra parameters instead"));
                }
                List<String> exceptions = resultNode.unwrapError();
                if (exceptions.isEmpty()) exceptions.add("Incorrect argument '" + reader.substring(reader.index()) + "'");
                String rootCommand = CommandCore.getInstance().getOptions().getRootCommand();
                String label = context.label();
                String correctSection = ChatColor.GRAY + rootCommand + " " + label + " " + reader.substring(0, reader.index());
                String incorrectParameter = ChatColor.RED.toString() + ChatColor.UNDERLINE + reader.substring(reader.index());
                exceptions.add(correctSection + incorrectParameter + ChatColor.RESET + ChatColor.RED + ChatColor.ITALIC + " <--[HERE]");
                exceptions.add("");
                return Result.error(exceptions);
            }
            CommandNode<?> node = resultNode.unwrap();
            CommandContext newContext = buildContext(context, current);
            if (node.getExecutor() == null) return Result.error(Collections.emptyList());
            Result<None, String> executeResult = node.executeWith(newContext);
            if (executeResult.isError()) {
                source.sender().sendMessage(ChatColor.RED + executeResult.unwrapError());
                return Result.ok();
            }
            return Result.ok();
        }

        if (executor == null) return Result.error(Collections.singletonList("Expected parameters, but got no parameters instead"));
        Result<None, String> requireResult = requires.stream().map(requirement -> requirement.testRequirement(context)).reduce(Result.ok(), Result::and);
        if (requireResult.isError()) {
            source.sender().sendMessage(ChatColor.RED + requireResult.unwrapError());
            return Result.ok();
        }
        executor.execute(context, source, args);
        return Result.ok();
    }

    @Override
    public @NotNull TabComplete tabComplete(@NotNull CommandContext context) {
        Arguments args = context.arguments();

        if (args.size() >= 1) {
            ArgumentReader reader = args.asReader();
            StringBuilder combinedArgs = new StringBuilder();
            for (int i = 0; i < args.size(); i++) {
                combinedArgs.append(args.getString(i)).append(" ");
            }
            combinedArgs.deleteCharAt(combinedArgs.length() - 1);
            CommandContext newContext = new CommandContext(context.source(), context.command(), context.label(), new StringArguments(Collections.singletonList(combinedArgs.toString())));
            List<CommandNode<?>> nodeSet = nodes;

            String matchingString = context.arguments().getString(context.arguments().size() - 1);

            if (args.size() != 1) {
                CurrentNode current = findCurrentNode(reader);
                List<CommandNode<?>> nodeList = current.nodes;
                if (nodeList.size() != 0) {
                    CommandNode<?> node = nodeList.get(nodeList.size() - 1);
                    newContext = buildContext(context, current);
                    nodeSet = node.getChildren();
                }
                matchingString = current.args.get(current.args.size() - 1).stringArg();
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
        return new CommandContext(context.source(), context.command(), context.label(), new ParsedArguments(result.args));
    }

    private @NotNull Result<ApplicableNode<?>, List<String>> checkApplicable(@NotNull ArgumentReader reader, @NotNull List<CommandNode<?>> nodes) {
        List<ApplicableNode<?>> options = new ArrayList<>();
        List<String> exceptions = new ArrayList<>();
        if (reader.index() != 0 && !reader.atEnd()) reader.next();
        int beforeIndex = reader.index();
        for (CommandNode<?> node : nodes) {
            node.parse(reader).match(val -> {
                String stringArg = reader.substring(beforeIndex, reader.index() + 1);
                options.add(new ApplicableNode<>(node, new ParsedArgument<>(val, stringArg), reader.index()));
            }, e -> {
                if (!e.isEmpty()) exceptions.add(e);
            });
            reader.jumpTo(beforeIndex);
        }
        if (options.isEmpty()) return Result.error(exceptions);
        ApplicableNode<?> option = options.get(0);
        reader.jumpTo(option.afterIndex);
        return Result.ok(option);
    }

    @Contract("_ -> new")
    private @NotNull CurrentNode findCurrentNode(@NotNull ArgumentReader reader) {
        List<CommandNode<?>> nodeSet = nodes;
        List<ParsedArgument<?>> args = new ArrayList<>();
        List<CommandNode<?>> nodeList = new ArrayList<>();
        CommandNode<?> node;
        do {
            if (nodeSet.isEmpty())
                return new CurrentNode(Result.error(Collections.emptyList()), nodeList, args, true);
            Result<ApplicableNode<?>, List<String>> parseResult = checkApplicable(reader, nodeSet);
            if (parseResult.isError()) {
                String rest = reader.substring(reader.index());
                args.add(new ParsedArgument<>(rest, rest));
                return new CurrentNode(Result.error(parseResult.unwrapError()), nodeList, args, false);
            }
            ApplicableNode<?> applicableNode = parseResult.unwrap();
            node = applicableNode.node;
            nodeSet = node.getChildren();
            nodeList.add(node);
            args.add(applicableNode.argument);

            if (reader.atEnd() && reader.get() == ' ') args.add(new ParsedArgument<>("", ""));
        } while (!reader.atEnd());
        return new CurrentNode(Result.ok(node), nodeList, args, false);
    }

    private record ApplicableNode<T>(CommandNode<?> node, ParsedArgument<T> argument, int afterIndex) { }

    private record CurrentNode(Result<CommandNode<?>, List<String>> node, List<CommandNode<?>> nodes, List<ParsedArgument<?>> args, boolean extraInput) { }
}
