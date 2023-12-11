package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.resultapi.Result;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

class JsonArgumentType<T> implements ArgumentType<T> {
    private static final Gson GSON = new Gson();

    private final Class<T> clazz;
    private final Type type;

    public JsonArgumentType(Class<T> clazz) {
        this.clazz = clazz;
        this.type = clazz;
    }
    public JsonArgumentType(@NotNull Class<T> clazz, @NotNull Type type) {
        if (!clazz.getTypeName().equals(type.getTypeName())) throw new IllegalArgumentException("'class' and 'type' must be the of the same type");
        this.clazz = clazz;
        this.type = type;
    }

    @Override
    public @NotNull String getName() {
        return "JSON";
    }

    @Override
    public @NotNull Result<T, String> parse(@NotNull ArgumentReader reader) {
        return NAME.parse(reader).andThen(str -> Result.resolve(() -> GSON.fromJson(str, type), e -> "Invalid JSON syntax: " + Throwables.getRootCause(e).getMessage()));
    }

    @Override
    public @NotNull Class<T> getArgumentClass() {
        return clazz;
    }
}
