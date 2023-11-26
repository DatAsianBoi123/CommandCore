package com.datasiqn.commandcore.argument.type;

import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import com.google.common.primitives.Primitives;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

class NumberArgumentType<T extends Number> implements SimpleArgumentType<T> {
    private final Class<T> numberClass;
    private final Class<T> primitiveClass;
    private final Parser<T> parser;

    public NumberArgumentType(Class<T> numberClass) {
        Class<T> primitiveClass = Primitives.unwrap(numberClass);
        if (!primitiveClass.isPrimitive()) throw new IllegalArgumentException(numberClass.getName() + " is an invalid number class");
        this.numberClass = numberClass.isPrimitive() ? Primitives.wrap(numberClass) : numberClass;
        this.primitiveClass = primitiveClass;

        // this is safe because all number wrapper classes have a static `valueOf` method
        try {
            Method valueOf = this.numberClass.getMethod("valueOf", String.class);
            this.parser = str -> Result.resolve(() -> {
                try {
                    //noinspection unchecked
                    return (T) valueOf.invoke(null, str);
                } catch (IllegalAccessException e) {
                    Bukkit.getLogger().severe("[CommandCore] could not parse number. please report this!");
                    e.printStackTrace();
                    return null;
                }
            });
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull String getName() {
        return primitiveClass.getName();
    }

    @Override
    public @NotNull Class<T> getArgumentClass() {
        return numberClass;
    }

    @Override
    public @NotNull Result<T, None> parseWord(String word) {
        return parser.parse(word);
    }

    private interface Parser<T> {
        Result<T, None> parse(String str);
    }
}