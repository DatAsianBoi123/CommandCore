package com.datasiqn.commandcore;

import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.arguments.impl.ArgumentsImpl;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.CommandOutput;
import com.datasiqn.commandcore.commands.CommandResult;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import com.datasiqn.commandcore.managers.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CommandCore implements org.bukkit.command.CommandExecutor, TabCompleter {
    private static CommandCore instance;
    private final CommandManager commandManager = new CommandManager();
    private final JavaPlugin plugin;
    private final org.bukkit.command.Command bukkitCommand;

    private CommandCore(JavaPlugin plugin, org.bukkit.command.Command command) {
        this.plugin = plugin;
        this.bukkitCommand = command;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public static @NotNull CommandCore init(JavaPlugin plugin, String rootCommand) {
        if (instance != null) throw new RuntimeException("An instance of CommandCore has already been created!");

        PluginCommand command = plugin.getCommand(rootCommand);
        if (command == null) {
            Bukkit.getLogger().info("[CommandCore] The root command " + rootCommand + " isn't registered in your plugin.yml! Attempting to reflectively insert it into Bukkit's command map...");
            try {
                Field mapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                mapField.setAccessible(true);
                CommandMap commandMap = (CommandMap) mapField.get(Bukkit.getServer());
                Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                constructor.setAccessible(true);
                command = constructor.newInstance(plugin.getName(), plugin);
                commandMap.register(rootCommand, plugin.getName(), command);
            } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException |
                     InstantiationException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            Bukkit.getLogger().info("[CommandCore] Successfully injected the command into Bukkit");
        }

        instance = new CommandCore(plugin, command);
        command.setExecutor(instance);
        command.setTabCompleter(instance);

        instance.commandManager.registerCommand("help", new CommandBuilder<>(CommandSender.class)
                .description("Shows the help menu")
                .then(ArgumentBuilder.argument(ArgumentType.COMMAND, "command")
                        .executes(context -> instance.sendCommandHelp(context.getSender(), context.parseArgument(ArgumentType.STRING, 0))))
                .executes(instance::sendHelpMenu)
                .build());

        return instance;
    }

    public static CommandCore getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length >= 1) {
            Command cmd = commandManager.getCommand(args[0]);
            if (cmd == null) {
                sendHelpMenu(sender);
                return true;
            }
            if (cmd.getPermissionString() != null && !sender.hasPermission(cmd.getPermissionString())) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return true;
            }
            List<String> listArgs = new ArrayList<>(Arrays.asList(args));
            listArgs.remove(0);
            CommandOutput output = cmd.getExecutor().execute(sender, new ArgumentsImpl(listArgs));
            if (output.getResult() == CommandResult.FAILURE) {
                for (String message : output.getMessages()) sender.sendMessage(ChatColor.RED + message);
                sender.sendMessage(ChatColor.GRAY + "Usage(s):");
                sender.sendMessage(getUsagesFor(args[0], 1).toArray(new String[0]));
            }
            return true;
        }
        sendHelpMenu(sender);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
        List<String> tabComplete = new ArrayList<>();
        if (args.length == 1) {
            commandManager.allCommands().forEach((s, cmd) -> {
                if (cmd.getPermissionString() == null || sender.hasPermission(cmd.getPermissionString())) tabComplete.add(s);
            });
        } else {
            Command cmd = commandManager.getCommand(args[0]);
            if (cmd == null || (cmd.getPermissionString() != null && !sender.hasPermission(cmd.getPermissionString()))) return new ArrayList<>();
            List<String> listArgs = new ArrayList<>(Arrays.asList(args));
            listArgs.remove(0);
            tabComplete.addAll(cmd.getExecutor().tabComplete(sender, new ArgumentsImpl(listArgs)));
        }

        List<String> partialMatches = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], tabComplete, partialMatches);
        partialMatches.sort(Comparator.naturalOrder());

        return partialMatches;
    }

    public void sendCommandHelp(@NotNull CommandSender sender, @NotNull String commandName) {
        if (!commandManager.hasCommand(commandName)) throw new RuntimeException("Command " + commandName + " does not exist");
        Command command = commandManager.getCommand(commandName);
        sender.sendMessage(ChatColor.GOLD + "Command " + commandName,
                ChatColor.GRAY + " Description: " + ChatColor.WHITE + command.getDescription(),
                ChatColor.GRAY + " Usage(s):");
        sender.sendMessage(getUsagesFor(commandName, 2).toArray(new String[0]));
    }

    public void sendHelpMenu(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + plugin.getName() + " Commands");
        commandManager.allCommands().keySet().stream().sorted().forEach(name -> {
            Command command = commandManager.getCommand(name);
            if (command.getPermissionString() == null || sender.hasPermission(command.getPermissionString())) sender.sendMessage(ChatColor.YELLOW + " " + name, ChatColor.GRAY + "  ↳ " + command.getDescription());
        });
    }

    private @NotNull List<String> getUsagesFor(String commandName, int spaces) {
        if (!commandManager.hasCommand(commandName)) throw new RuntimeException("Command " + commandName + " does not exist");
        List<String> usages = new ArrayList<>();
        Command command = commandManager.getCommand(commandName);
        command.getUsages().forEach(usage -> {
            StringBuilder addedUsage = new StringBuilder();
            for (int i = 0; i < spaces; i++) {
                addedUsage.append(" ");
            }
            addedUsage.append(ChatColor.YELLOW)
                    .append("/")
                    .append(this.bukkitCommand.getName())
                    .append(" ").append(ChatColor.WHITE).append(commandName)
                    .append(" ").append(usage);
            usages.add(addedUsage.toString());
        });
        return usages;
    }
}
