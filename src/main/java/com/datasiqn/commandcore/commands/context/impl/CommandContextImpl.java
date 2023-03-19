package com.datasiqn.commandcore.commands.context.impl;

import com.datasiqn.commandcore.arguments.Arguments;
import com.datasiqn.commandcore.commands.CommandSource;
import com.datasiqn.commandcore.commands.context.CommandContext;
import org.jetbrains.annotations.NotNull;

public class CommandContextImpl implements CommandContext {
    private final CommandSource source;
    private final Arguments arguments;

    public CommandContextImpl(CommandSource source, Arguments arguments) {
        this.source = source;
        this.arguments = arguments;
    }

    @Override
    public @NotNull CommandSource getSource() {
        return source;
    }

    @Override
    public @NotNull Arguments getArguments() {
        return arguments;
    }
}
