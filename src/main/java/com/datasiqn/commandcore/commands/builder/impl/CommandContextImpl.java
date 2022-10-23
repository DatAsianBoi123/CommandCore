package com.datasiqn.commandcore.commands.builder.impl;

import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.commands.builder.CommandContext;
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
        return type.fromString(arguments.get(index)).orElseThrow(IllegalArgumentException::new);
    }
}
