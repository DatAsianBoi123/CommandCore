package com.datasiqn.commandcore;

import com.datasiqn.commandcore.argument.ListArguments;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.TabComplete;
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
            Result<None, List<String>> output = cmd.getExecutor().execute(CommandCore.createContext(CommandCore.createSource(sender), cmd, args[0], new ListArguments(listArgs)));
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
        String matchingString = args[args.length - 1];
        if (args.length == 1) {
            commandCore.getCommandManager().allCommands().forEach((s, cmd) -> {
                if (cmd.getPermissionString() == null || sender.hasPermission(cmd.getPermissionString())) tabComplete.add(s);
            });
        } else {
            Command cmd = commandCore.getCommandManager().getCommand(args[0]);
            if (cmd == null || (cmd.getPermissionString() != null && !sender.hasPermission(cmd.getPermissionString()))) return new ArrayList<>();
            List<String> listArgs = new ArrayList<>(Arrays.asList(args));
            listArgs.remove(0);
            TabComplete complete = cmd.getExecutor().getTabComplete(CommandCore.createContext(CommandCore.createSource(sender), cmd, args[0], new ListArguments(listArgs)));
            matchingString = complete.getMatchingString();
            tabComplete.addAll(complete.values());
        }

        List<String> partialMatches = new ArrayList<>();
        StringUtil.copyPartialMatches(matchingString, tabComplete, partialMatches);
        partialMatches.sort(Comparator.naturalOrder());

        return partialMatches;
    }
}
