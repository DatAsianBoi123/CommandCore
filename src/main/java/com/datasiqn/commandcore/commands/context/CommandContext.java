package com.datasiqn.commandcore.commands.context;

import com.datasiqn.commandcore.arguments.ArgumentType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CommandContext<S extends CommandSender> {
    S getSender();

    List<String> getArguments();

    <T> T parseArgument(@NotNull ArgumentType<T> type, int index);
}
