package com.datasiqn.commandcore.commands;

import com.datasiqn.commandcore.arguments.Arguments;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface CommandExecutor {
    CommandOutput execute(@NotNull CommandSender sender, @NotNull Arguments args);

    @NotNull
    default List<String> tabComplete(@NotNull CommandSender sender, @NotNull Arguments args) {
        return new ArrayList<>();
    }
}
