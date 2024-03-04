package com.datasiqn.commandcore;

import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.source.*;
import com.datasiqn.commandcore.managers.CommandManager;
import com.datasiqn.commandcore.managers.HelpManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
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
    private final HelpManager helpManager;
    private final JavaPlugin plugin;
    private final org.bukkit.command.Command bukkitCommand;
    private final InitOptions options;

    private CommandCore(JavaPlugin plugin, org.bukkit.command.Command command, @NotNull InitOptions options) {
        this.helpManager = new HelpManager(options.getCommandsPerPage());
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
     * Gets the help manager
     * @return The help manager
     */
    public HelpManager getHelpManager() {
        return helpManager;
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
     * Sends the 1st page of the help menu to {@code sender}
     * @param sender The sender
     */
    public void sendHelpMenu(@NotNull CommandSender sender) {
        sendHelpMenu(sender, 1);
    }
    /**
     * Sends a specific page of the help menu to {@code sender}
     * @param sender The sender
     * @param page The page, starting at 1
     * @throws IllegalArgumentException If {@code page} is {@literal <} 1 or {@literal >} the total number of pages
     */
    public void sendHelpMenu(@NotNull CommandSender sender, int page) {
        HelpManager.HelpPage helpPage = helpManager.getHelpPage(page, name -> {
            Command command = commandManager.getCommand(name, false);
            //noinspection ConstantConditions
            return !command.hasPermission() || sender.hasPermission(command.getPermissionString());
        });
        sender.sendMessage("-".repeat(20));
        String pluginName = options.hasCustomPluginName() ? options.getPluginName() : plugin.getName();
        String pageCounter = ChatColor.GRAY + "[" + ChatColor.WHITE + helpPage.page() + ChatColor.GRAY + "/" + ChatColor.YELLOW + helpPage.totalPages() + ChatColor.GRAY + "]";
        sender.sendMessage(ChatColor.GOLD + pluginName + " Help " + pageCounter + ChatColor.WHITE);
        for (String name : helpPage.names()) {
            Command command = commandManager.getCommand(name, false);
            String description = command.hasDescription() ? command.getDescription() : "No description provided";
            sender.sendMessage(ChatColor.DARK_GRAY + "/" + ChatColor.WHITE + options.getRootCommand() + ChatColor.YELLOW + " " + name, ChatColor.GRAY + description);
        }
        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append("<<").color(ChatColor.DARK_GRAY).bold(true);
        if (page > 1) {
            componentBuilder
                    .color(ChatColor.GOLD)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + options.getRootCommand() + " help " + (page - 1)))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Previous page")));
        }

        componentBuilder
                .append("       ").reset()
                .append(">>").color(ChatColor.DARK_GRAY).bold(true);

        if (page < helpPage.totalPages()) {
            componentBuilder
                    .color(ChatColor.GOLD)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + options.getRootCommand() + " help " + (page + 1)))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Next page")));
        }
        sender.spigot().sendMessage(componentBuilder.create());
        sender.sendMessage("-".repeat(20));
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
     * @return This instance
     * @throws IllegalStateException If {@code CommandCore} hasn't been initialized yet with {@link #init(JavaPlugin, InitOptions) init}
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

        if (options.createHelpCommand()) instance.commandManager.registerCommand(HelpCommand.createCommand());

        return instance;
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
