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
        if (reader.atEnd()) return Result.error("missing end quote");
        reader.next();
        ArgumentReader.ReadUntilResult readUntil = reader.readUntilEscaped('"');
        if (!readUntil.foundEnd()) return Result.error("missing end quote");
        if (!reader.atEnd()) {
            if (reader.next() != ' ') return Result.error("cannot have extra characters after end quote");
        }
        return Result.ok(readUntil.getRead());
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        Arguments arguments = context.arguments();
        String arg = arguments.getString(arguments.size() - 1);
        ArgumentReader reader = new StringArgumentReader(arg);
        if (reader.size() == 0) return Collections.singletonList("\"");
        if (reader.get() != '"') return ArgumentType.super.getTabComplete(context);
        if (reader.size() > 1) {
            ArgumentReader.ReadUntilResult readUntil = reader.readUntilEscaped('"');
            if (!readUntil.foundEnd()) return Collections.singletonList(arg + "\"");
        } else {
            return Collections.singletonList("\"\"");
        }
        return ArgumentType.super.getTabComplete(context);
    }

    @Override
    public @NotNull Class<String> getArgumentClass() {
        return String.class;
    }
}
