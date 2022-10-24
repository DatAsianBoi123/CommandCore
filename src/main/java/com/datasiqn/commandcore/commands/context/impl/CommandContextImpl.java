package com.datasiqn.commandcore.commands.context.impl;

import com.datasiqn.commandcore.ArgumentParseException;
import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.commands.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandContextImpl<S extends CommandSender> implements CommandContext<S> {
    private final S sender;
    private final List<String> arguments;

    public CommandContextImpl(S sender, List<String> arguments) {
        this.sender = sender;
        this.arguments = arguments;
    }

    public S getSender() {
        return sender;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public <T> T parseArgument(@NotNull ArgumentType<T> type, int index) {
        try {
            return type.parse(arguments.get(index));
        } catch (ArgumentParseException e) {
            throw new RuntimeException(e);
        }
    }
}