package com.datasiqn.commandcore.commands;

import com.datasiqn.commandcore.arguments.Arguments;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface Command {
    CommandOutput execute(@NotNull CommandSender sender, @NotNull Arguments args);

    @Nullable
    String getPermissionString();

    @NotNull
    String getDescription();

    List<String> getUsages();

    @NotNull
    default List<String> tabComplete(@NotNull CommandSender sender, @NotNull Arguments args) {
        return new ArrayList<>();
    }
}
