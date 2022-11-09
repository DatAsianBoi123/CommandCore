package com.datasiqn.commandcore.commands.builder;

import com.datasiqn.commandcore.ArgumentParseException;
import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.result.Result;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents an argument node
 * @param <S> The type of the sender
 * @param <T> The type of the argument
 */
public class ArgumentBuilder<S extends CommandSender, T> extends CommandNode<S, ArgumentBuilder<S, T>> {
    private final ArgumentType<T> type;
    private final String argName;

    private ArgumentBuilder(ArgumentType<T> type, String argName) {
        this.type = type;
        this.argName = argName;
    }

    @Override
    public @NotNull List<String> getTabComplete() {
        return type.getTabComplete();
    }

    @Override
    public @NotNull Result<?, ArgumentParseException> parse(String arg) {
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
    protected @NotNull ArgumentBuilder<S, T> getThis() {
        return this;
    }

    /**
     * Creates a new {@code ArgumentBuilder}
     * @param type The argument type
     * @param argName The name of the argument
     * @return The created {@code ArgumentBuilder} instance
     * @param <S> The type of the sender
     * @param <T> The type of the argument
     */
    @Contract("_, _ -> new")
    public static <S extends CommandSender, T> @NotNull ArgumentBuilder<S, T> argument(ArgumentType<T> type, String argName) {
        return new ArgumentBuilder<>(type, argName);
    }
}
