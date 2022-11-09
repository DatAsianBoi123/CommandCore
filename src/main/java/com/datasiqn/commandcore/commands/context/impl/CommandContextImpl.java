package com.datasiqn.commandcore.commands.context.impl;

import com.datasiqn.commandcore.arguments.Arguments;
import com.datasiqn.commandcore.commands.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandContextImpl<S extends CommandSender> implements CommandContext<S> {
    private final S sender;
    private final Arguments arguments;

    public CommandContextImpl(S sender, Arguments arguments) {
        this.sender = sender;
        this.arguments = arguments;
    }

    public @NotNull S getSender() {
        return sender;
    }

    public @NotNull Arguments getArguments() {
        return arguments;
    }
}
