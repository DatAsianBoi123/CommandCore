package com.datasiqn.commandcore.arguments;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.util.ParseUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface ArgumentType<T> {
    ArgumentType<String> STRING = new StringArgumentType();
    ArgumentType<Integer> NATURAL_NUMBER = new CustomArgumentType<>(str -> {
        Integer integer = ParseUtil.parseInt(str);
        if (integer == null) return null;
        if (integer <= 0) return null;
        return integer;
    });
    ArgumentType<Boolean> BOOLEAN = new CustomArgumentType<>(ParseUtil::parseBoolean, Arrays.asList("true", "false"));
    ArgumentType<Player> PLAYER = new CustomArgumentType<>(Bukkit::getPlayerExact, () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
    ArgumentType<Command> COMMAND = new CustomArgumentType<>(str -> CommandCore.getInstance().getCommandManager().getCommand(str), () -> new ArrayList<>(CommandCore.getInstance().getCommandManager().allCommands().keySet()));

    @NotNull
    Optional<T> fromString(@NotNull String str);

    @NotNull
    default List<String> all() {
        return new ArrayList<>();
    }

    class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {
        private final Class<T> enumClass;

        public EnumArgumentType(Class<T> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public @NotNull Optional<T> fromString(@NotNull String str) {
            try {
                return Optional.of(T.valueOf(enumClass, str.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        @Override
        public @NotNull List<String> all() {
            return Arrays.stream(enumClass.getEnumConstants()).map(t -> t.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());
        }
    }

    class CustomArgumentType<T> implements ArgumentType<T> {
        private final Function<String, T> asStringFunction;
        private List<String> values;
        private Supplier<List<String>> valueSupplier;

        public CustomArgumentType(Function<String, T> asStringFunction) {
            this(asStringFunction, Collections.emptyList());
        }
        public CustomArgumentType(Function<String, T> asStringFunction, List<String> values) {
            this.asStringFunction = asStringFunction;
            this.values = values;
        }
        public CustomArgumentType(Function<String, T> asStringFunction, Supplier<List<String>> valueSupplier) {
            this.asStringFunction = asStringFunction;
            this.valueSupplier = valueSupplier;
        }

        @Override
        public @NotNull Optional<T> fromString(@NotNull String str) {
            return Optional.ofNullable(asStringFunction.apply(str));
        }

        @Override
        public @NotNull List<String> all() {
            return values == null ? valueSupplier.get() : values;
        }
    }

    class StringArgumentType implements ArgumentType<String> {

        @Override
        public @NotNull Optional<String> fromString(@NotNull String str) {
            return Optional.of(str);
        }
    }
}
