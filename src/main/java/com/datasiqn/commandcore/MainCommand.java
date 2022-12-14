package com.datasiqn.commandcore;

import com.datasiqn.commandcore.arguments.impl.ArgumentsImpl;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.context.impl.CommandContextImpl;
import com.datasiqn.commandcore.commands.context.impl.CommandSourceImpl;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {
    private final CommandCore commandCore;

    public MainCommand(CommandCore commandCore) {
        this.commandCore = commandCore;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length >= 1) {
            Command cmd = commandCore.getCommandManager().getCommand(args[0]);
            if (cmd == null) {
                commandCore.sendHelpMenu(sender);
                return true;
            }
            if (cmd.getPermissionString() != null && !sender.hasPermission(cmd.getPermissionString())) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            List<String> listArgs = new ArrayList<>(Arrays.asList(args));
            listArgs.remove(0);
            @NotNull Result<None, List<String>> output = cmd.getExecutor().execute(new CommandContextImpl(new CommandSourceImpl(sender), new ArgumentsImpl(listArgs)));
            output.ifError(messages -> {
                for (String message : messages) sender.sendMessage(ChatColor.RED + message);
                sender.sendMessage(ChatColor.GRAY + "Usage(s):");
                sender.sendMessage(commandCore.getUsagesFor(args[0], 1).toArray(new String[0]));
            });
            return true;
        }
        commandCore.sendHelpMenu(sender);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> tabComplete = new ArrayList<>();
        if (args.length == 1) {
            commandCore.getCommandManager().allCommands().forEach((s, cmd) -> {
                if (cmd.getPermissionString() == null || sender.hasPermission(cmd.getPermissionString())) tabComplete.add(s);
            });
        } else {
            Command cmd = commandCore.getCommandManager().getCommand(args[0]);
            if (cmd == null || (cmd.getPermissionString() != null && !sender.hasPermission(cmd.getPermissionString()))) return new ArrayList<>();
            List<String> listArgs = new ArrayList<>(Arrays.asList(args));
            listArgs.remove(0);
            tabComplete.addAll(cmd.getExecutor().tabComplete(new CommandContextImpl(new CommandSourceImpl(sender), new ArgumentsImpl(listArgs))));
        }

        List<String> partialMatches = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], tabComplete, partialMatches);
        partialMatches.sort(Comparator.naturalOrder());

        return partialMatches;
    }
}
