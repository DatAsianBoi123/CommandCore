package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.StringArgumentReader;
import com.datasiqn.commandcore.argument.numrange.*;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

class NumberRangeArgumentType<T extends Number & Comparable<T>> implements SimpleArgumentType<NumberRange<T>> {
    private final NumberArgumentType<T> numberArgumentType;

    public NumberRangeArgumentType(Class<T> numberClass) {
        numberArgumentType = NumberArgumentType.number(numberClass);
    }

    @Override
    public @NotNull String getName() {
        return numberArgumentType.getPrimitiveClass().getName() + " range";
    }

    @Override
    public @NotNull Result<NumberRange<T>, None> parseWord(@NotNull String word) {
        ArgumentReader reader = new StringArgumentReader(word);
        String startString = readUntilDoublePeriod(reader);
        T start = null;
        if (!startString.isEmpty()) {
            Result<T, None> parseStartResult = numberArgumentType.parseWord(startString);
            if (parseStartResult.isError()) return parseStartResult.map(num -> null);
            start = parseStartResult.unwrap();
        }
        if (reader.get() != '.') return Result.ok(new SingleNumberRange<>(start));
        T end = null;
        if (!reader.atEnd()) {
            reader.next();
            String endString = reader.rest();
            if (!endString.isEmpty()) {
                Result<T, None> parseEndResult = numberArgumentType.parseWord(endString);
                if (parseEndResult.isError()) return parseEndResult.map(num -> null);
                end = parseEndResult.unwrap();
            }
        }

        if (start == null && end == null) return Result.ok(new FullNumberRange<>());
        if (end == null) return Result.ok(new FromNumberRange<>(start));
        if (start == null) return Result.ok(new ToNumberRange<>(end));
        return Result.ok(new FromToNumberRange<>(start, end));
    }

    @Override
    public @NotNull Class<NumberRange<T>> getArgumentClass() {
        //noinspection unchecked,UnstableApiUsage
        return (Class<NumberRange<T>>) new TypeToken<NumberRange<T>>() {}.getRawType();
    }

    @NotNull
    private static String readUntilDoublePeriod(@NotNull ArgumentReader reader) {
        if (reader.size() == 0) return "";
        if (reader.atEnd()) return String.valueOf(reader.get());
        StringBuilder builder = new StringBuilder();
        builder.append(reader.get());
        char prev = reader.get();
        while (!reader.atEnd()) {
            char next = reader.next();
            if (next == '.' && prev == '.') {
                builder.deleteCharAt(builder.length() - 1);
                break;
            }
            builder.append(next);
            prev = next;
        }
        return builder.toString();
    }
}
