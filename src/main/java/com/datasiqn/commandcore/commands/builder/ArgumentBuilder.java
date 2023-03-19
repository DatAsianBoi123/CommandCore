package com.datasiqn.commandcore.commands.builder;

import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.commands.context.CommandContext;
import com.datasiqn.resultapi.Result;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents an argument node
 * @param <T> The type of the argument
 */
public class ArgumentBuilder<T> extends CommandNode<ArgumentBuilder<T>> {
    private final ArgumentType<T> type;
    private final String argName;

    private ArgumentBuilder(ArgumentType<T> type, String argName) {
        this.type = type;
        this.argName = argName;
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return type.getTabComplete(context);
    }

    @Override
    public @NotNull Result<?, String> parse(String arg) {
        return type.parse(arg);
    }

    @Override
    protected String getUsageArgument(boolean isOptional) {
        return isOptional ? ChatColor.GREEN + "[" + argName + "]" : ChatColor.GOLD + "<" + argName + ">";
    }

    @Override
    protected int getPriority() {
        return 5;
    }

    @Override
    protected boolean canBeOptional() {
        return true;
    }

    @Override
    protected @NotNull ArgumentBuilder<T> getThis() {
        return this;
    }

    /**
     * Creates a new {@code ArgumentBuilder}
     * @param type The argument type
     * @param argName The name of the argument
     * @return The created {@code ArgumentBuilder} instance
     * @param <T> The type of the argument
     */
    @Contract("_, _ -> new")
    public static <T> @NotNull ArgumentBuilder<T> argument(ArgumentType<T> type, String argName) {
        return new ArgumentBuilder<>(type, argName);
    }
}
