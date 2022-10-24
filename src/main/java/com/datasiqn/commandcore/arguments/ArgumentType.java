package com.datasiqn.commandcore.arguments;

import com.datasiqn.commandcore.ArgumentParseException;
import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface ArgumentType<T> {
    ArgumentType<String> STRING = new StringArgumentType();

    ArgumentType<Integer> INTEGER = new CustomArgumentType<>(str -> {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException("Invalid integer " + str);
        }
    });

    ArgumentType<Integer> NATURAL_NUMBER = new CustomArgumentType<>(str -> {
        int integer;
        try {
            integer = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new ArgumentParseException("Invalid integer " + str);
        }
        if (integer <= 0) throw new ArgumentParseException("Integer must not be below 0");
        return integer;
    });

    ArgumentType<Boolean> BOOLEAN = new CustomArgumentType<>(str -> {
        if (str.equalsIgnoreCase("true")) return true;
        else if (str.equalsIgnoreCase("false")) return false;
        throw new ArgumentParseException("Invalid boolean " + str + ", expected either true or false");
    }, Arrays.asList("true", "false"));

    ArgumentType<Player> PLAYER = new CustomArgumentType<>(name -> {
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) throw new ArgumentParseException("No player exists with the name " + name);
        return player;
    }, () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

    ArgumentType<Command> COMMAND = new CustomArgumentType<>(str -> {
        Command executor = CommandCore.getInstance().getCommandManager().getCommand(str);
        if (executor == null) throw new ArgumentParseException("No command exists with the name " + str);
        return executor;
    }, () -> new ArrayList<>(CommandCore.getInstance().getCommandManager().allCommands().keySet()));

    @NotNull
    T parse(@NotNull String str) throws ArgumentParseException;

    @NotNull
    default List<String> getTabComplete() {
        return new ArrayList<>();
    }

    class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {
        private final Class<T> enumClass;

        public EnumArgumentType(Class<T> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public @NotNull T parse(@NotNull String str) throws ArgumentParseException {
            try {
                return T.valueOf(enumClass, str.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ArgumentParseException("Invalid " + enumClass.getEnclosingClass().getTypeName() + " '" + str + "'");
            }
        }

        @Override
        public @NotNull List<String> getTabComplete() {
            return Arrays.stream(enumClass.getEnumConstants()).map(t -> t.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());
        }
    }

    class CustomArgumentType<T> implements ArgumentType<T> {
        private final ParseFunction<T> asStringFunction;
        private List<String> values;
        private Supplier<List<String>> valueSupplier;

        public CustomArgumentType(ParseFunction<T> parseFunction) {
            this(parseFunction, Collections.emptyList());
        }
        public CustomArgumentType(ParseFunction<T> asStringFunction, List<String> values) {
            this.asStringFunction = asStringFunction;
            this.values = values;
        }
        public CustomArgumentType(ParseFunction<T> parseFunction, Supplier<List<String>> valueSupplier) {
            this.asStringFunction = parseFunction;
            this.valueSupplier = valueSupplier;
        }

        @Override
        public @NotNull T parse(@NotNull String str) throws ArgumentParseException {
            return asStringFunction.apply(str);
        }

        @Override
        public @NotNull List<String> getTabComplete() {
            return values == null ? valueSupplier.get() : values;
        }

        @FunctionalInterface
        private interface ParseFunction<T> {
            T apply(String str) throws ArgumentParseException;
        }
    }

    class StringArgumentType implements ArgumentType<String> {
        @Override
        public @NotNull String parse(@NotNull String str) {
            return str;
        }
    }
}
