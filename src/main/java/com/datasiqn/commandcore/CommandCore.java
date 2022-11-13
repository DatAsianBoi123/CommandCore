package com.datasiqn.commandcore;

import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import com.datasiqn.commandcore.managers.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class of {@code CommandCore}
 */
public class CommandCore {
    private static CommandCore instance;
    private final CommandManager commandManager = new CommandManager();
    private final JavaPlugin plugin;
    private final org.bukkit.command.Command bukkitCommand;
    private final InitOptions options;

    private CommandCore(JavaPlugin plugin, org.bukkit.command.Command command, InitOptions options) {
        this.plugin = plugin;
        this.bukkitCommand = command;
        this.options = options;
    }

    /**
     * Gets the command manager
     * @return The command manager
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Initializes CommandCore so that it can be accessed using {@link CommandCore#getInstance()}
     *
     * @param plugin Your plugin instance
     * @param rootCommand The name of your root command
     * @return The instance
     * @throws RuntimeException If it has already been initialized
     */
    public static @NotNull CommandCore init(JavaPlugin plugin, String rootCommand) {
        return init(plugin, InitOptions.Builder.create(rootCommand).build());
    }

    /**
     * Initializes CommandCore so that it can be accessed using {@link CommandCore#getInstance()}
     *
     * @param plugin Your plugin instance
     * @param options The initialization options
     * @return The instance
     * @throws RuntimeException If it has already been initialized
     */
    public static @NotNull CommandCore init(JavaPlugin plugin, InitOptions options) {
        if (instance != null) throw new RuntimeException("An instance of CommandCore has already been created");

        String rootCommand = options.getRootCommand();
        PluginCommand command = plugin.getCommand(rootCommand);
        if (command == null) {
            Bukkit.getLogger().info("[CommandCore] The root command " + rootCommand + " isn't registered in your plugin.yml! Attempting to reflectively insert it into Bukkit's command map...");
            try {
                Field mapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                mapField.setAccessible(true);
                CommandMap commandMap = (CommandMap) mapField.get(Bukkit.getServer());
                Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                constructor.setAccessible(true);
                command = constructor.newInstance(rootCommand, plugin);
                commandMap.register(rootCommand, plugin.getName(), command);
            } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException |
                     InstantiationException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            Bukkit.getLogger().info("[CommandCore] Successfully injected the command into Bukkit");
        }

        instance = new CommandCore(plugin, command, options);
        MainCommand mainCommand = new MainCommand(instance);
        command.setExecutor(mainCommand);
        command.setTabCompleter(mainCommand);

        if (options.createHelpCommand()) instance.commandManager.registerCommand("help", new CommandBuilder<>(CommandSender.class)
                .description("Shows the help menu")
                .then(ArgumentBuilder.argument(ArgumentType.COMMAND, "command")
                        .executes(context -> instance.sendCommandHelp(context.getSender(), context.getArguments().getString(0))))
                .executes(instance::sendHelpMenu));

        return instance;
    }

    /**
     * Gets this instance of {@code CommandCore}
     * @return This instance
     */
    public static CommandCore getInstance() {
        return instance;
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
        sender.sendMessage(ChatColor.GOLD + (options.hasCustomPluginName() ? options.getPluginName() : plugin.getName()) + " Commands");
        commandManager.allCommands().keySet().stream().sorted().forEach(name -> {
            Command command = commandManager.getCommand(name);
            if (command.getPermissionString() == null || sender.hasPermission(command.getPermissionString())) sender.sendMessage(ChatColor.YELLOW + " " + name, ChatColor.GRAY + "  ↳ " + command.getDescription());
        });
    }

    @NotNull
    public List<String> getUsagesFor(String commandName, int spaces) {
        if (!commandManager.hasCommand(commandName))
            throw new RuntimeException("Command " + commandName + " does not exist");
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
