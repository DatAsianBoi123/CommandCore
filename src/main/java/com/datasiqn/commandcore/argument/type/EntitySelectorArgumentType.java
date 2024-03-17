package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.argument.StringArgumentReader;
import com.datasiqn.commandcore.argument.StringArguments;
import com.datasiqn.commandcore.argument.selector.*;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class EntitySelectorArgumentType<E extends Entity> implements ArgumentType<EntitySelector<E>> {
    private static final SelectorType[] SELECTOR_TYPES = SelectorType.values();

    private final SelectorRequirements<E> requirements;

    public EntitySelectorArgumentType(SelectorRequirements<E> requirements) {
        this.requirements = requirements;
    }

    @Override
    public @NotNull String getName() {
        return "entity selector";
    }

    @Override
    public @NotNull Result<EntitySelector<E>, String> parse(@NotNull ArgumentReader reader) {
        if (reader.get() == '@') return parseEntitySelector(reader);

        String word = reader.nextWord();

        Class<E> entityClass = requirements.getEntityClass();
        if (entityClass.isAssignableFrom(Player.class)) {
            Result<EntitySelector<Player>, String> parseOnlinePlayerResult = parseOnlinePlayer(word);
            if (parseOnlinePlayerResult.isOk()) {
                // this is safe because the Player is an instance of E
                //noinspection unchecked
                return parseOnlinePlayerResult.map(selector -> (EntitySelector<E>) selector);
            }
        }

        return parseEntity(word).andThen(selector -> {
            if (entityClass.isAssignableFrom(selector.getFirst(null).getClass())) {
                // this is safe because the selected entity is an instance of E
                //noinspection unchecked
                return Result.ok((EntitySelector<E>) selector);
            }
            return Result.error("selector includes an invalid entity");
        });
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        List<String> tabcomplete = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toCollection(ArrayList::new));

        Arguments arguments = context.arguments();
        String arg = arguments.getString(arguments.size() - 1);

        SelectorType selectorType = null;
        for (SelectorType type : SELECTOR_TYPES) {
            String selector = "@" + type.getChar();

            if (arg.startsWith(selector)) {
                selectorType = type;
                break;
            }

            tabcomplete.add(selector);
        }
        if (selectorType == null) return tabcomplete;

        String selectorString = "@" + selectorType.getChar();
        ArgumentReader reader = new StringArgumentReader(arg);
        reader.next();
        if (reader.atEnd()) return Collections.singletonList(selectorString + "[");
        char next = reader.next();
        if (next != '[') return Collections.emptyList();
        if (reader.atEnd()) return createOptionNames(selectorString + "[");

        int beforeOptionIndex = reader.index() + 1;
        while (reader.get() != ']') {
            String before = reader.substring(0, beforeOptionIndex);
            if (reader.atEnd()) return createOptionNames(before);
            if (reader.next() == ']') break;
            String optionName = reader.readUntil('=', ']');
            if (reader.get() == ']') return Collections.emptyList();
            if (reader.get() != '=') return createOptionNames(before);
            int beforeValueIndex = reader.index() + 1;
            SelectorOptionType<?> optionType = SelectorOptionType.getAllOptions().get(optionName);
            if (!reader.atEnd()) reader.next();
            ArgumentReader.ReadUntilResult readUntil = reader.readUntilEscaped(',', ']');
            if (readUntil.foundEnd()) {
                if (reader.get() == ']') break;
                if (reader.get() == ',') {
                    beforeOptionIndex = reader.index() + 1;
                    continue;
                }
            }
            ArgumentType<?> argumentType = optionType.getArgumentType();
            String value = readUntil.getRead();
            if (argumentType.parse(new StringArgumentReader(value)).isOk()) return List.of(arg + ",", arg + "]");
            CommandContext newContext = new CommandContext(context.source(), context.command(), context.label(), new StringArguments(Collections.singletonList(value)));
            return argumentType.getTabComplete(newContext).stream().map(str -> reader.substring(0, beforeValueIndex) + str).toList();

        }
        return ArgumentType.super.getTabComplete(context);
    }

    @Override
    public @NotNull Class<EntitySelector<E>> getArgumentClass() {
        //noinspection unchecked,UnstableApiUsage
        return (Class<EntitySelector<E>>) new TypeToken<EntitySelector<E>>() {}.getRawType();
    }

    private List<String> createOptionNames(String prefix) {
        return SelectorOptionType.getAllOptions().keySet().stream().map(str -> prefix + str + "=").toList();
    }

    private @NotNull Result<EntitySelector<Player>, String> parseOnlinePlayer(@NotNull String word) {
        return Result.ofNullable(Bukkit.getPlayerExact(word), "player not found with name '" + word + "'")
                .orElse(none -> Result.resolve(() -> java.util.UUID.fromString(word), e -> "invalid uuid '" + word + "'")
                        .andThen(uuid -> Result.ofNullable(Bukkit.getPlayer(uuid), "player not found with uuid '" + uuid + "'")))
                .map(SingleEntitySelector::new);
    }

    private @NotNull Result<EntitySelector<Entity>, String> parseEntity(@NotNull String word) {
        return Result.resolve(() -> java.util.UUID.fromString(word), e -> "invalid uuid '" + word + "'")
                .andThen(uuid -> Result.ofNullable(Bukkit.getEntity(uuid), "entity not found with uuid '" + uuid + "'"))
                .map(SingleEntitySelector::new);
    }

    private Result<EntitySelector<E>, String> parseEntitySelector(@NotNull ArgumentReader reader) {
        if (reader.atEnd()) return Result.error("expected character after '@', but found none");
        char selectorChar = reader.next();
        SelectorType currentSelector = Arrays.stream(SELECTOR_TYPES).filter(type -> selectorChar == type.getChar()).findFirst().orElse(null);

        if (currentSelector == null) return Result.error("unknown selector type '" + selectorChar + "'");

        SelectorOptions options = currentSelector.getDefaultOptions();
        if (!reader.atEnd()) {
            char next = reader.next();
            if (next != '[' && next != ' ') return Result.error("selector type must only be 1 character long");
            if (next == '[') {
                while (!reader.atEnd() && reader.get() != ']') {
                    if (reader.next() == ']') break;
                    String optionName = reader.readUntil('=', ']');
                    if (reader.get() == ']' || reader.get() != '=')
                        return Result.error("expected an '=' after option name, but found none");
                    if (reader.atEnd() || reader.next() == ']')
                        return Result.error("expected value after equal sign, but found none");
                    SelectorOptionType<?> optionType = SelectorOptionType.getAllOptions().get(optionName);
                    if (optionType == null) return Result.error("unknown option '" + optionName + "'");
                    String value = reader.readUntilEscaped(',', ']').getRead();
                    Result<?, String> parseResult = optionType.getArgumentType().parse(new StringArgumentReader(value));
                    if (parseResult.isError()) return parseResult.map(val -> null);
                    optionType.uncheckedSet(options, parseResult.unwrap());
                }
                if (reader.get() != ']') return Result.error("expected closing brackets, but found none");
                if (!reader.atEnd() && reader.next() != ' ')
                    return Result.error("unexpected trailing characters after closing brackets");
            }
        }

        Result<None, String> allowsOptions = requirements.allows(options);
        if (allowsOptions.isError()) return allowsOptions.map(none -> null);

        return Result.ok(new MultiEntitySelector<>(options, requirements.getEntityClass()));
    }
}
