package com.datasiqn.commandcore;

import com.datasiqn.commandcore.argument.annotation.BoundedInt;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.annotation.*;
import com.datasiqn.commandcore.command.source.CommandSource;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandDescription(name = "help", description = "Shows the help menu")
class HelpCommand implements AnnotationCommand {
    @Executor
    public void help(CommandSender sender) {
        CommandCore.getInstance().sendHelpMenu(sender);
    }

    @LiteralExecutor("command")
    public void command(@NotNull CommandSource source, @Argument(name = "command") Command command) {
        if (!source.hasPermission(command.getPermissionString())) {
            source.sendMessage(ChatColor.RED + "No help for " + command.getName());
            return;
        }
        CommandCore.getInstance().sendCommandHelp(source.getSender(), command);
    }

    @LiteralExecutor("page")
    public void page(@NotNull CommandSender sender, @Argument(name = "page") @BoundedInt(min = 1) int page) {
        try {
            CommandCore.getInstance().sendHelpMenu(sender, page);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Page number out of bounds");
        }
    }
}
