package com.datasiqn.commandcore.commands.builder;

import com.datasiqn.commandcore.ArgumentParseException;
import com.datasiqn.resultapi.Result;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a literal node
 * @param <S> The type of the sender
 */
public class LiteralBuilder<S extends CommandSender> extends CommandNode<S, LiteralBuilder<S>> {
    private final String literal;

    private LiteralBuilder(String literal) {
        this.literal = literal;
    }

    @Override
    public @NotNull Result<String, ArgumentParseException> parse(String arg) {
        return Result.<String, ArgumentParseException>ok(arg).andThen(str -> {
            if (literal.equals(arg)) return Result.ok(str);
            return Result.error(new ArgumentParseException("Invalid literal '" + arg + "'"));
        });
    }

    @Override
    public @NotNull List<String> getTabComplete() {
        return new ArrayList<>(Collections.singletonList(literal));
    }

    @Override
    protected String getUsageArgument(boolean isOptional) {
        return ChatColor.WHITE + literal;
    }

    @Override
    protected int getPriority() {
        return 10;
    }

    @Override
    protected @NotNull LiteralBuilder<S> getThis() {
        return this;
    }

    /**
     * Creates a new {@link LiteralBuilder}
     * @param literal The literal string
     * @return The created {@link LiteralBuilder} instance
     * @param <S> The type of the sender
     */
    @Contract("_ -> new")
    public static <S extends CommandSender> @NotNull LiteralBuilder<S> literal(String literal) {
        return new LiteralBuilder<>(literal);
    }
}
