package com.datasiqn.commandcore.commands.builder;

import com.datasiqn.commandcore.ArgumentParseException;
import com.datasiqn.commandcore.arguments.ArgumentType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    public @Nullable ArgumentParseException getParsingException(String arg) {
        try {
            type.parse(arg);
        } catch (ArgumentParseException e) {
            return e;
        }
        return null;
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

    @Contract("_, _ -> new")
    public static <S extends CommandSender, T> @NotNull ArgumentBuilder<S, T> argument(ArgumentType<T> type, String argName) {
        return new ArgumentBuilder<>(type, argName);
    }
}
