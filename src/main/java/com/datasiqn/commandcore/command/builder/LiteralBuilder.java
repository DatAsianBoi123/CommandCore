package com.datasiqn.commandcore.command.builder;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.Result;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a literal node
 */
public class LiteralBuilder extends CommandNode<LiteralBuilder> {
    private final String literal;

    private LiteralBuilder(String literal) {
        this.literal = literal;
    }

    @Override
    public @NotNull Result<String, String> parse(@NotNull ArgumentReader reader) {
        return Result.<String, String>ok(reader.nextWord()).andThen(str -> literal.equals(str) ? Result.ok(str) : Result.error(""));
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return new ArrayList<>(Collections.singletonList(literal));
    }

    @Override
    public String toString() {
        return "Literal(" + literal + ")";
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
    protected @NotNull LiteralBuilder getThis() {
        return this;
    }

    /**
     * Creates a new {@link LiteralBuilder}
     * @param literal The literal string. String cannot be empty or have spaces
     * @throws IllegalArgumentException If the string is empty or contains spaces
     * @return The created {@link LiteralBuilder} instance
     */
    @Contract("_ -> new")
    public static @NotNull LiteralBuilder literal(@NotNull String literal) {
        if (literal.isEmpty()) throw new IllegalArgumentException("literal string cannot be empty");
        if (literal.contains(" ")) throw new IllegalArgumentException("literal string cannot have spaces");
        return new LiteralBuilder(literal);
    }
}
