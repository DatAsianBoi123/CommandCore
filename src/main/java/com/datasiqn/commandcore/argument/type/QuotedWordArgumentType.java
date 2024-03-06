package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.argument.StringArgumentReader;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

class QuotedWordArgumentType implements ArgumentType<String> {
    @Override
    public @NotNull String getName() {
        return "quoted word";
    }

    @Override
    public @NotNull Result<String, String> parse(@NotNull ArgumentReader reader) {
        if (reader.get() != '"') return Result.error("expected quotes");
        StringBuilder builder = new StringBuilder();
        boolean foundEndQuote = false;
        Character prev = null;
        while (!reader.atEnd()) {
            char next = reader.next();
            if (next == '"') {
                if (prev == null || prev != '\\') {
                    foundEndQuote = true;
                    break;
                }
                builder.deleteCharAt(builder.length() - 1);
            }
            if (next == '\\' && prev != null && prev == '\\') {
                prev = null;
                continue;
            }
            builder.append(next);
            prev = next;
        }
        if (!foundEndQuote) return Result.error("missing end quote");
        if (!reader.atEnd()) {
            if (reader.next() != ' ') return Result.error("cannot have extra characters after end quote");
        }
        return Result.ok(builder.toString());
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        Arguments arguments = context.arguments();
        String arg = arguments.getString(arguments.size() - 1);
        ArgumentReader reader = new StringArgumentReader(arg);
        if (arg.isEmpty()) return Collections.singletonList("\"");
        if (!arg.startsWith("\"")) return ArgumentType.super.getTabComplete(context);
        if (arg.length() > 1) {
            boolean foundEndQuote = false;
            Character prev = null;
            while (!reader.atEnd()) {
                char next = reader.next();
                if (next == '"') {
                    if (prev == null || prev != '\\') {
                        foundEndQuote = true;
                        break;
                    }
                }
                if (next == '\\' && prev != null && prev == '\\') {
                    prev = null;
                    continue;
                }
                prev = next;
            }
            if (!foundEndQuote) {
                return Collections.singletonList(arg + "\"");
            }
        }
        return ArgumentType.super.getTabComplete(context);
    }

    @Override
    public @NotNull Class<String> getArgumentClass() {
        return String.class;
    }
}
