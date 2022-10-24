package com.datasiqn.commandcore.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Command {
    @NotNull CommandExecutor getExecutor();

    @Nullable String getPermissionString();

    @NotNull String getDescription();

    List<String> getUsages();
}
