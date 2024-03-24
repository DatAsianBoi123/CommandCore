package com.datasiqn.commandcore;

import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.source.CommandSource;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

class HelpCommand {
    public static CommandBuilder createCommand() {
        CommandCore instance = CommandCore.getInstance();
        return new CommandBuilder("help")
                .description("Shows the help menu")
                .then(ArgumentBuilder.argument(ArgumentType.COMMAND_NAME, "command")
                        .executes((context, source, arguments) -> {
                            Command command = arguments.get(0, ArgumentType.COMMAND_NAME);
                            if (!source.hasPermission(command.getPermissionString())) {
                                source.sendMessage(ChatColor.RED + "No help for " + command.getName());
                                return;
                            }
                            instance.sendCommandHelp(source.getSender(), command);
                        }))
                .then(ArgumentBuilder.argument(ArgumentType.number(int.class), "page")
                        .executes(HelpCommand::sendHelp))
                .executes(HelpCommand::sendHelp);
    }

    private static void sendHelp(CommandContext context, @NotNull CommandSource source, @NotNull Arguments arguments) {
        int page;
        if (arguments.size() == 0) page = 1;
        else page = arguments.getChecked(0, ArgumentType.boundedNumber(int.class, 1)).unwrapOr(1);
        CommandCore.getInstance().sendHelpMenu(source.getSender(), page);
    }
}
