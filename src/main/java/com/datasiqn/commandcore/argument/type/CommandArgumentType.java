package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.argument.ExecutableCommand;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class CommandArgumentType implements ArgumentType<ExecutableCommand> {
    private static final CommandMap COMMAND_MAP;
    static {
        CommandMap commandMap;
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            commandMap = null;
        }
        COMMAND_MAP = commandMap;
    }

    @Override
    public @NotNull String getName() {
        return "command";
    }

    @Override
    public @NotNull Result<ExecutableCommand, String> parse(@NotNull ArgumentReader reader) {
        String rest = reader.rest();
        // stops CommandCore from adding an extra empty tab complete
        if (rest.endsWith(" ")) return Result.error("unreachable");
        return Result.ok(sender -> Bukkit.dispatchCommand(sender, rest));
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        if (COMMAND_MAP == null) {
            Bukkit.getLogger().warning("[CommandCore] Could not tabcomplete because Bukkit's command map was not found! Please report this!");
            return Collections.emptyList();
        }
        Arguments arguments = context.arguments();
        String command = arguments.getString(arguments.size() - 1);
        String[] split = command.split(" ", -1);
        List<String> tabComplete = COMMAND_MAP.tabComplete(context.source().getSender(), command);
        if (tabComplete == null) return Collections.emptyList();
        if (split.length == 1) return tabComplete.stream().map(complete -> complete.substring(1)).toList();
        String previousArgs = Arrays.stream(split).limit(split.length - 1).collect(Collectors.joining(" "));
        return tabComplete.stream().map(complete -> previousArgs + " " + complete).toList();
    }

    @Override
    public @NotNull Class<ExecutableCommand> getArgumentClass() {
        return ExecutableCommand.class;
    }
}
