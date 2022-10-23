package com.datasiqn.commandcore.commands.builder;

import com.datasiqn.commandcore.arguments.ArgumentType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
        return type.all();
    }

    @Override
    public boolean isApplicable(String arg) {
        return type.fromString(arg).isPresent();
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
