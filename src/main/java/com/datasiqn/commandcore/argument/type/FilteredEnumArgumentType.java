package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a custom {@code ArgumentType} that parses to a filtered enum
 *
 * @param <T> The type of the enum
 */
public class FilteredEnumArgumentType<T extends Enum<T>> extends EnumArgumentType<T> {
    private final Predicate<T> filter;
    private final List<String> tabCompletes;

    /**
     * Creates a new {@code ArgumentType}
     *
     * @param enumClass The enum's class
     * @param filter    The filter that enum values must pass through
     * @param enumName  The name of the enum. This is used when displaying an error message (Invalid {{@code enumName}} '{val}'
     */
    public FilteredEnumArgumentType(@NotNull Class<T> enumClass, Predicate<T> filter, String enumName) {
        super(enumClass, enumName);
        this.filter = filter;
        this.tabCompletes = Arrays.stream(enumClass.getEnumConstants()).filter(filter).map(val -> val.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());
    }

    @Override
    public @NotNull Result<T, None> parseWord(String word) {
        return super.parseWord(word).andThen(val -> filter.test(val) ? Result.ok(val) : Result.error());
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return tabCompletes;
    }
}
