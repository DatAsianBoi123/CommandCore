package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class CommandArgumentType implements SimpleArgumentType<Command> {
    @Override
    public @NotNull String getTypeName() {
        return "command";
    }

    @Override
    public @NotNull Result<Command, None> parseWord(String word) {
        return Result.ofNullable(CommandCore.getInstance().getCommandManager().getCommand(word), None.NONE);
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        List<String> commandNames = new ArrayList<>();
        CommandCore.getInstance().getCommandManager().allCommands().forEach((name, command) -> {
            if (context.getSource().hasPermission(command.getPermissionString())) commandNames.add(name);
        });
        return commandNames;
    }
}
