package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.argument.StringArgumentReader;
import com.datasiqn.commandcore.argument.duration.Duration;
import com.datasiqn.commandcore.argument.duration.TimeUnit;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class DurationArgumentType implements ArgumentType<Duration> {
    private static final TimeUnit[] TIME_UNITS = TimeUnit.values();

    @Override
    public @NotNull String getName() {
        return "duration";
    }

    @Override
    public @NotNull Result<Duration, String> parse(@NotNull ArgumentReader reader) {
        if (reader.atEnd()) return Result.error("missing duration/unit");
        StringBuilder durationStringBuilder = new StringBuilder(String.valueOf(reader.get()));
        while (true) {
            char next = reader.next();
            if (!Character.isDigit(next) && next != '-' && next != '.') break;
            durationStringBuilder.append(next);
            if (reader.atEnd()) return Result.error("missing unit");
        }

        String durationString = durationStringBuilder.toString();
        return Result.resolve(() -> Double.parseDouble(durationString), err -> "invalid duration '" + durationString + "'")
                .andThen(duration -> {
                    if (duration < 0) return Result.error("duration cannot be negative");
                    String unitSymbol = reader.rest();
                    for (TimeUnit timeUnit : TIME_UNITS) {
                        if (unitSymbol.equals(timeUnit.getSymbol())) {
                            return Result.ok(Duration.from(duration, timeUnit));
                        }
                    }
                    return Result.error("unknown time unit '" + unitSymbol + "'");
                });
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        Arguments arguments = context.arguments();
        ArgumentReader reader = new StringArgumentReader(arguments.getString(arguments.size() - 1));
        if (reader.size() == 0) return Collections.emptyList();
        StringBuilder durationStringBuilder = new StringBuilder(String.valueOf(reader.get()));
        while (!reader.atEnd()) {
            char next = reader.next();
            if (!Character.isDigit(next) && next != '-' && next != '.') break;
            durationStringBuilder.append(next);
        }

        String durationString = durationStringBuilder.toString();
        try {
            if (Double.parseDouble(durationString) < 0) return Collections.emptyList();
        } catch (NumberFormatException ignored) {
            return Collections.emptyList();
        }
        return Arrays.stream(TIME_UNITS).map(timeUnit -> durationString + timeUnit.getSymbol()).toList();
    }

    @Override
    public @NotNull Class<Duration> getArgumentClass() {
        return Duration.class;
    }
}
