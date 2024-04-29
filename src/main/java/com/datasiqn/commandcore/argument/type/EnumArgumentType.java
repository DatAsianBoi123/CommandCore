package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Represents a custom {@code ArgumentType} that parses to an enum value
 *
 * @param <T> The type of the enum
 */
public class EnumArgumentType<T extends Enum<T>> implements SimpleArgumentType<T> {
    private final Class<T> enumClass;
    private final String enumName;
    private final List<String> tabCompletes;
    private final boolean uppercaseValues;

    /**
     * Creates a new {@code ArgumentType}
     *
     * @param enumClass The enum's class
     */
    public EnumArgumentType(@NotNull Class<T> enumClass) {
        this(enumClass, enumClass.getSimpleName());
    }

    /**
     * Creates a new {@code ArgumentType}
     *
     * @param enumClass The enum's class
     * @param enumName  The name of the enum
     */
    public EnumArgumentType(@NotNull Class<T> enumClass, @NotNull String enumName) {
        this.enumClass = enumClass;
        this.enumName = enumName;
        this.tabCompletes = Arrays.stream(enumClass.getEnumConstants()).map(val -> val.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());

        for (T enumConstant : enumClass.getEnumConstants()) {
            for (char letter : enumConstant.name().toCharArray()) {
                if (Character.isLetter(letter) && !Character.isUpperCase(letter)) {
                    this.uppercaseValues = false;
                    Bukkit.getLogger().warning("[CommandCore] Enum " + enumName + " includes values that aren't in uppercase!");
                    return;
                }
            }
        }
        this.uppercaseValues = true;
    }

    @Override
    public @NotNull String getName() {
        return enumName;
    }

    @Override
    public @NotNull Result<T, None> parseWord(String word) {
        return Result.resolve(() -> uppercaseValues ? Enum.valueOf(enumClass, word.toUpperCase()) : EnumUtils.findEnumInsensitiveCase(enumClass, word));
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return tabCompletes;
    }

    @Override
    public @NotNull Class<T> getArgumentClass() {
        return enumClass;
    }
}
