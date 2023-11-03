package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.commandcore.managers.CommandManager;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class CommandArgumentType implements SimpleArgumentType<Command> {
    @Override
    public @NotNull String getName() {
        return "command";
    }

    @Override
    public @NotNull Result<Command, None> parseWord(String word) {
        return Result.ofNullable(CommandCore.getInstance().getCommandManager().getCommand(word, false), None.NONE);
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        List<String> commandNames = new ArrayList<>();
        CommandManager manager = CommandCore.getInstance().getCommandManager();
        manager.getCommandNames(false).forEach(name -> {
            Command command = manager.getCommand(name, false);
            if (context.source().hasPermission(command.getPermissionString())) commandNames.add(name);
        });
        return commandNames;
    }

    @Override
    public @NotNull Class<Command> getArgumentClass() {
        return Command.class;
    }
}
