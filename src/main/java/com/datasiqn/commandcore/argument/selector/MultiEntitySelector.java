package com.datasiqn.commandcore.argument.selector;

import com.datasiqn.commandcore.argument.numrange.NumberRange;
import com.datasiqn.commandcore.command.source.CommandSource;
import com.datasiqn.commandcore.locatable.LocatableCommandSender;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a selection of 1 or more entities. Any entity selector that starts with the at (@) symbol is a {@code MultiEntitySelector}.
 * @param <E> The type of the entity
 */
public class MultiEntitySelector<E extends Entity> implements EntitySelector<E> {
    private final SelectorOptions options;

    /**
     * Creates a new {@code MultiEntitySelector} with the options of {@code options} and an entity class of {@code entityClass}
     * @param options The selector options that are used when finding entities
     * @param entityClass The class of the entities being selected
     * @throws IllegalArgumentException When {@code options} has a {@link SelectorOptionType#TYPE TYPE} that does not extend {@code E}.
     * This can be checked for by using {@link SelectorRequirements#entityTypeExtendsClass(EntityType, Class)}.
     */
    public MultiEntitySelector(@NotNull SelectorOptions options, Class<E> entityClass) {
        if (!SelectorRequirements.entityTypeExtendsClass(options.get(SelectorOptionType.TYPE), entityClass)) {
            throw new IllegalArgumentException("selected entities do not extend E (" + entityClass.getName() + ")");
        }

        this.options = options;
    }

    @Override
    @NotNull
    public List<E> get(@Nullable CommandSource source) {
        List<Entity> entities = Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntities().stream())
                .filter(entity -> nullOrPasses(SelectorOptionType.TYPE, type -> entity.getType() == type) &&
                        nullOrEquals(SelectorOptionType.NAME, entity.getCustomName()) &&
                        nullOrEquals(SelectorOptionType.WORLD, entity.getWorld()) &&
                        distWithinRange(SelectorOptionType.DX, source, entity, Location::getX) &&
                        distWithinRange(SelectorOptionType.DY, source, entity, Location::getY) &&
                        distWithinRange(SelectorOptionType.DZ, source, entity, Location::getZ) &&
                        locatableNullOrPasses(SelectorOptionType.DISTANCE, source, (locatable, range) -> {
                            Location sourceLocation = locatable.getLocation();
                            Location entityLocation = entity.getLocation();
                            if (!Objects.equals(sourceLocation.getWorld(), entityLocation.getWorld())) return false;
                            return range.contains(sourceLocation.distance(entityLocation));
                        }))
                .collect(Collectors.toCollection(ArrayList::new));

        options.get(SelectorOptionType.SORT).getOrder().order(source, entities);

        //noinspection unchecked
        return (List<E>) entities.stream().limit(options.get(SelectorOptionType.LIMIT)).toList();
    }

    private boolean distWithinRange(SelectorOptionType<NumberRange<Double>> type, @Nullable CommandSource source, Entity entity, Function<Location, Double> getDouble) {
        return locatableNullOrPasses(type, source, (locatable, range) -> {
            Location sourceLocation = locatable.getLocation();
            Location entityLocation = entity.getLocation();
            if (!Objects.equals(sourceLocation.getWorld(), entityLocation.getWorld())) return false;
            double sourceNum = getDouble.apply(sourceLocation);
            double entityNum = getDouble.apply(entityLocation);
            return range.contains(entityNum - sourceNum);
        });
    }

    private <T> boolean locatableNullOrPasses(SelectorOptionType<T> type, @Nullable CommandSource source, BiPredicate<LocatableCommandSender, T> predicate) {
        return nullOrPasses(type, val -> {
            if (source == null) return false;
            Result<LocatableCommandSender, String> locatableResult = source.getLocatableChecked();
            if (locatableResult.isError()) return false;
            return predicate.test(locatableResult.unwrap(), val);
        });
    }

    private <T> boolean nullOrEquals(SelectorOptionType<T> type, T equals) {
        return nullOrPasses(type, val -> val.equals(equals));
    }

    private <T> boolean nullOrPasses(SelectorOptionType<T> type, Predicate<T> predicate) {
        T val = options.get(type);
        return val == null || predicate.test(val);
    }
}
