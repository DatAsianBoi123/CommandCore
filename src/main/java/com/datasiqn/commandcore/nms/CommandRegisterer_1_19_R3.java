package com.datasiqn.commandcore.nms;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.argument.StringArguments;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.commandcore.command.CommandSource;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.builder.CommandLink;
import com.datasiqn.commandcore.command.builder.CommandLink.Executor;
import com.datasiqn.commandcore.command.builder.CommandNode;
import com.datasiqn.commandcore.command.builder.LiteralBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class CommandRegisterer_1_19_R3 extends CommandRegisterer {
    private static final Map<String, ArgumentType<?>> CREATED_TYPES = new HashMap<>();
    private final CommandDispatcher<CommandListenerWrapper> dispatcher;
    private final LiteralArgumentBuilder<CommandListenerWrapper> root;

    public CommandRegisterer_1_19_R3(String rootName) {
        super(rootName);
        root = net.minecraft.commands.CommandDispatcher.a(rootName);
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        try {
            Field consoleField = CraftServer.class.getDeclaredField("console");
            consoleField.setAccessible(true);
            DedicatedServer console = (DedicatedServer) consoleField.get(craftServer);
            dispatcher = console.vanillaCommandDispatcher.a();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addCommand(@NotNull CommandBuilder command) {
        LiteralArgumentBuilder<CommandListenerWrapper> literal = LiteralArgumentBuilder.literal(command.getName());
        Executor executor = command.getExecutor();
        com.datasiqn.commandcore.command.Command builtCommand = command.build();
        if (executor != null) {
            literal.executes(toCommand(executor, builtCommand));
        }
        addChildrenRecursive(literal, command, builtCommand);
        root.then(literal);
    }

    private void addChildrenRecursive(ArgumentBuilder<CommandListenerWrapper, ?> argumentBuilder, @NotNull CommandLink<?> link, com.datasiqn.commandcore.command.Command command) {
        for (CommandNode<?> node : link.getChildren()) {
            ArgumentBuilder<CommandListenerWrapper, ?> builder;
            if (node instanceof LiteralBuilder literal) builder = LiteralArgumentBuilder.literal(literal.getLiteral());
            else if (node instanceof com.datasiqn.commandcore.command.builder.ArgumentBuilder<?> argument) {
                builder = RequiredArgumentBuilder.argument(argument.getName(), toArgumentType(argument.getType(), command));
            } else throw new RuntimeException("command node not yet implemented");
            Executor executor = node.getExecutor();
            if (executor != null) {
                builder.executes(toCommand(executor, command));
            }
            addChildrenRecursive(builder, node, command);
            argumentBuilder.then(builder);
        }
    }

    @Contract(pure = true)
    private @NotNull Command<CommandListenerWrapper> toCommand(Executor executor, com.datasiqn.commandcore.command.Command command) {
        return context -> {
            executor.execute(toContext(context, command));
            return Command.SINGLE_SUCCESS;
        };
    }

    @Override
    public @NotNull CommandContext toContext(com.mojang.brigadier.context.@NotNull CommandContext<?> context, com.datasiqn.commandcore.command.Command command) {
        CommandSource source = CommandCore.createSource(((CommandListenerWrapper) context.getSource()).getBukkitSender());
        Pair<String, List<String>> splitCommand = splitCommand(context.getInput());
        source.sendMessage("Label is: '" + splitCommand.getLeft() + "'");
        source.sendMessage("Args is: '" + String.join(",", splitCommand.getRight()) + "'");
        return CommandCore.createContext(source, command, splitCommand.getLeft(), new StringArguments(splitCommand.getRight()));
    }

    private <T> ArgumentType<T> toArgumentType(com.datasiqn.commandcore.argument.type.@NotNull ArgumentType<T> type, com.datasiqn.commandcore.command.Command command) {
        //noinspection unchecked
        return (ArgumentType<T>) CREATED_TYPES.computeIfAbsent(type.getName(), k -> {
//            ArgumentType<T> argumentType = generateArgumentType(type, command);
            System.out.println("argument type " + k + " was not found. recreating");
            try {
                // unfreeze command argument registry
                IRegistry<ArgumentTypeInfo<?, ?>> argumentTypeRegistry = BuiltInRegistries.w;
                Field frozenField = RegistryMaterials.class.getDeclaredField("l");
                frozenField.setAccessible(true);
                frozenField.set(argumentTypeRegistry, false);

                // reset tags
                Field tagField = RegistryMaterials.class.getDeclaredField("k");
                tagField.setAccessible(true);
                tagField.set(argumentTypeRegistry, new IdentityHashMap<>());

                // register the created argument type
                Method register = ArgumentTypeInfos.class.getDeclaredMethod("a", IRegistry.class, String.class, Class.class, ArgumentTypeInfo.class);
                register.setAccessible(true);
                register.invoke(null, argumentTypeRegistry, type.getName(), type.getClass(), SingletonArgumentInfo.a(() -> type));

                // refreeze registry
                argumentTypeRegistry.l();
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            return type;
        });
    }

    @Override
    public void removeOld() {
        dispatcher.getRoot().removeCommand(rootName);
    }

    @Override
    public void register() {
        dispatcher.register(root);
    }
}
