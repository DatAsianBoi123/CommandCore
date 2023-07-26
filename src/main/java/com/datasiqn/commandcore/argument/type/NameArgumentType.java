package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

class NameArgumentType implements ArgumentType<String> {
    @Override
    public @NotNull Result<String, String> parse(@NotNull ArgumentReader reader) {
        StringBuilder builder = new StringBuilder();
        builder.append(reader.get());
        while (!reader.atEnd()) {
            builder.append(reader.next());
        }
        return Result.ok(builder.toString());
    }
}
