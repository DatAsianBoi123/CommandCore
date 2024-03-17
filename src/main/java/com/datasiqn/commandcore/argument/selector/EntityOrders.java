package com.datasiqn.commandcore.argument.selector;

import com.datasiqn.commandcore.locatable.LocatableCommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Represents a collection of builtin {@link EntityOrder}s
 */
public enum EntityOrders {
    /**
     * Entities are sorted from closest to farthest
     */
    NEAREST(distance(false)),
    /**
     * Entities are sorted farthest to closest
     */
    FARTHEST(distance(true)),
    /**
     * Entities are sorted arbitrarily. This usually means older entities are closer to the front, but no guarantees are made about the order of entities.
     */
    ARBITRARY((source, entities) -> {}),
    /**
     * Entities are sorted randomly. Behind the hood, this uses {@link Collections#shuffle(List)}.
     */
    RANDOM(((source, entities) -> Collections.shuffle(entities))),
    ;

    private final EntityOrder order;

    EntityOrders(EntityOrder order) {
        this.order = order;
    }

    /**
     * Gets the order
     * @return The order
     */
    public EntityOrder getOrder() {
        return order;
    }

    @Contract(pure = true)
    private static @NotNull EntityOrder distance(boolean flipped) {
        return EntityOrder.comparator((source, e1, e2) -> {
            if (source == null) return 0;
            var locatableChecked = source.getLocatableChecked();
            if (locatableChecked.isError()) return 0;
            LocatableCommandSender locatable = locatableChecked.unwrap();
            if (!e1.getWorld().equals(locatable.getWorld()) && !e2.getWorld().equals(locatable.getWorld())) return 0;
            if (!e1.getWorld().equals(locatable.getWorld())) return 1;
            if (!e2.getWorld().equals(locatable.getWorld())) return -1;
            double e1Dist = e1.getLocation().distanceSquared(locatable.getLocation());
            double e2Dist = e2.getLocation().distanceSquared(locatable.getLocation());
            return flipped ? Double.compare(e2Dist, e1Dist) : Double.compare(e1Dist, e2Dist);
        });
    }
}
