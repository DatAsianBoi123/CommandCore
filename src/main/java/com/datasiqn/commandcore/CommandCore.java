package com.datasiqn.commandcore;

import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.source.*;
import com.datasiqn.commandcore.managers.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
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
     * Gets the options used to initialize {@code CommandCore}
     * @return The options used to initialize {@code CommandCore}
     */
    public InitOptions getOptions() {
        return options;
    }

    /**
     * Sends command usage to {@code sender}
     * @param sender The sender
     * @param commandName The name of the command
     * @throws IllegalArgumentException If {@code commandName} is not the name of a command
     */
    public void sendCommandHelp(@NotNull CommandSender sender, @NotNull String commandName) {
        Command command = commandManager.getCommand(commandName, false);
        if (command == null) throw new IllegalArgumentException("Command " + commandName + " does not exist");
        sender.sendMessage(ChatColor.GOLD + "Command " + commandName,
                ChatColor.GRAY + " Description: " + ChatColor.WHITE + (command.hasDescription() ? command.getDescription() : "No description provided"),
                ChatColor.GRAY + " Aliases: [" + ChatColor.WHITE + String.join(ChatColor.GRAY + ", " + ChatColor.WHITE, command.getAliases()) + ChatColor.GRAY + "]",
                ChatColor.GRAY + " Usage(s):");
        sender.sendMessage(getUsagesFor(commandName, 2).toArray(new String[0]));
    }

    /**
     * Sends the help menu to {@code sender}
     * @param sender The sender
     */
    public void sendHelpMenu(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + (options.hasCustomPluginName() ? options.getPluginName() : plugin.getName()) + " Commands");
        commandManager.getCommandNames(false).stream().sorted().forEach(name -> {
            Command command = commandManager.getCommand(name, false);
            if (!command.hasPermission() || sender.hasPermission(command.getPermissionString())) {
                String description = command.hasDescription() ? command.getDescription() : "No description provided";
                sender.sendMessage(ChatColor.YELLOW + " " + name, ChatColor.GRAY + "  ↳ " + description);
            }
        });
    }

    /**
     * Generates a formatted string for each usage of a command
     * @param commandName The name of the command
     * @param spaces The # of spaces to add before each usage string
     * @return A list of formatted strings representing all usages for the command
     * @throws IllegalArgumentException If {@code commandName} is not the name of a command
     */
    @NotNull
    public List<String> getUsagesFor(String commandName, int spaces) {
        Command command = commandManager.getCommand(commandName, false);
        if (command == null) throw new IllegalArgumentException("Command " + commandName + " does not exist");
        List<String> usages = new ArrayList<>();
        command.getUsages().forEach(usage -> {
            String addedUsage = " ".repeat(Math.max(0, spaces)) +
                    ChatColor.YELLOW +
                    "/" +
                    this.bukkitCommand.getName() +
                    " " + ChatColor.WHITE + commandName +
                    " " + usage;
            usages.add(addedUsage);
        });
        return usages;
    }

    /**
     * Gets this instance of {@code CommandCore}
     * @throws IllegalStateException If {@code CommandCore} hasn't been initialized yet with {@link #init(JavaPlugin, InitOptions) init}
     * @return This instance
     */
    public static CommandCore getInstance() {
        if (instance == null) throw new IllegalStateException("CommandCore has not been initialized");
        return instance;
    }

    /**
     * Initializes CommandCore so that it can be accessed using {@link #getInstance()}
     * @param plugin Your plugin instance
     * @param rootCommand The name of your root command
     * @return The instance
     * @throws IllegalStateException If it has already been initialized
     */
    public static @NotNull CommandCore init(JavaPlugin plugin, String rootCommand) {
        return init(plugin, InitOptions.Builder.create(rootCommand).build());
    }

    /**
     * Initializes CommandCore so that it can be accessed using {@link #getInstance()}
     * @param plugin Your plugin instance
     * @param options The initialization options
     * @return The instance
     * @throws IllegalStateException If it has already been initialized
     */
    public static @NotNull CommandCore init(JavaPlugin plugin, InitOptions options) {
        if (instance != null) throw new IllegalStateException("An instance of CommandCore has already been created");

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
                command.setAliases(options.getAliases());
                command.setDescription("Base plugin command");
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

        if (options.createHelpCommand()) instance.commandManager.registerCommand(new CommandBuilder("help")
                .description("Shows the help menu")
                .then(ArgumentBuilder.argument(ArgumentType.COMMAND, "command")
                        .executes(context -> {
                            Command cmd = context.getArguments().get(0, ArgumentType.COMMAND);
                            String commandName = context.getArguments().getString(0);
                            if (!context.getSource().hasPermission(cmd.getPermissionString())) {
                                context.getSource().sendMessage(ChatColor.RED + "No help for " + commandName);
                                return;
                            }
                            instance.sendCommandHelp(context.getSource().sender(), commandName);
                        }))
                .executes(context -> instance.sendHelpMenu(context.getSource().sender())));

        return instance;
    }

    /**
     * Creates a new {@code CommandContext}
     * @param source The sender that executed the command
     * @param command The command being executed
     * @param label The exact string used to execute the command. This can either be the name of the command or one of its aliases
     * @param arguments The command arguments
     * @return The newly created {@code CommandContext}
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static @NotNull CommandContext createContext(CommandSource source, Command command, String label, Arguments arguments) {
        return new CommandContext() {
            @Override
            public @NotNull CommandSource getSource() {
                return source;
            }

            @Override
            public @NotNull Command getCommand() {
                return command;
            }

            @Override
            public @NotNull String getLabel() {
                return label;
            }

            @Override
            public @NotNull Arguments getArguments() {
                return arguments;
            }
        };
    }

    /**
     * Creates a new {@code CommandSource}
     * @param sender The source of the command
     * @return The newly created {@code CommandSource}
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull CommandSource createSource(CommandSender sender) {
        if (sender instanceof Player player) {
            return new PlayerCommandSource(player);
        } else if (sender instanceof Entity entity) {
            return new EntityCommandSource(entity);
        } else if (sender instanceof BlockCommandSender block) {
            return new BlockCommandSource(block);
        } else {
            return new GenericCommandSource(sender);
        }
    }
}
