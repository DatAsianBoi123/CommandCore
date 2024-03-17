package com.datasiqn.commandcore.argument.selector;

import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents requirements of a {@link EntitySelector}
 * @param <E> The type of the entity being selected
 */
public class SelectorRequirements<E extends Entity> {
    private final Class<E> entityClass;
    private final int limit;

    @Contract(pure = true)
    private SelectorRequirements(@NotNull Builder<E> builder) {
        this.entityClass = builder.entityClass;
        this.limit = builder.limit;
    }

    /**
     * Gets the class of the entities being selected
     * @return The entity class
     */
    public Class<E> getEntityClass() {
        return entityClass;
    }

    /**
     * Gets the maximum number of entities that can be selected
     * @return The maximum number of entities that can be selected
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Gets whether these requirements allow {@code options} or not
     * @param options The options to check
     * @return An {@code Ok} value if these requirements allow {@code options}, otherwise an {@code Error} value containing a string "error" message
     */
    public Result<None, String> allows(@NotNull SelectorOptions options) {
        if (this.limit < options.get(SelectorOptionType.LIMIT)) return Result.error("selector can't select more than " + this.limit + " entities");
        EntityType entityType = options.get(SelectorOptionType.TYPE);
        if (!entityTypeExtendsClass(entityType, entityClass)) return Result.error("selector contains disallowed entities");
        return Result.ok();
    }

    @Override
    public String toString() {
        return "SelectorRequirements(" +
                "selects=" + entityClass.getName() +
                ",limit=" + limit +
                ")";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        SelectorRequirements<?> that = (SelectorRequirements<?>) object;

        if (limit != that.limit) return false;
        return entityClass.equals(that.entityClass);
    }

    /**
     * Gets whether the entity described by {@code type} extends {@code entityClass}.
     * <pre>
     *     assert entityTypeExtendsClass(EntityType.PIG, LivingEntity.class);
     *     assert entityTypeExtendsClass(EntityType.PLAYER, Player.class);
     *     assert !entityTypeExtendsClass(EntityType.ITEM_FRAME, LivingEntity.class);
     * </pre>
     * @param type The entity type to check, or {@code null} to signify any entity
     * @param entityClass The entity class
     * @return {@code true} if an entity with the type {@code type} extends {@code entityClass}, {@code false} otherwise
     */
    @Contract("null, _ -> true")
    public static boolean entityTypeExtendsClass(@Nullable EntityType type, @NotNull Class<? extends Entity> entityClass) {
        Class<? extends Entity> typeClass = type == null ? Entity.class : type.getEntityClass();
        return typeClass != null && entityClass.isAssignableFrom(typeClass);
    }

    /**
     * Creates a new {@code SelectorRequirements} that allows just one entity that extends {@code entityClass}
     * @param entityClass The entity class that the entity must extend
     * @return The newly created {@code SelectorRequirements}
     * @param <E> The type the selected entities must extend
     */
    public static <E extends Entity> SelectorRequirements<E> allowOne(Class<E> entityClass) {
        return new Builder<>(entityClass).limit(1).build();
    }

    /**
     * Creates a new {@code SelectorRequirements} that allow any number of entities that extend {@code entityClass}
     * @param entityClass The entity class that the entities must extend
     * @return The newly created {@code SelectorRequirements}
     * @param <E> The type the selected entities must extend
     */
    public static <E extends Entity> SelectorRequirements<E> allowInfinite(Class<E> entityClass) {
        return new Builder<>(entityClass).build();
    }

    /**
     * Creates a builder that is used to create an instance of {@code SelectorRequirements}
     * @param entityClass The entity class that the selected entities must extend
     * @return The newly created {@code Builder}
     * @param <E> The type the selected entities must extend
     */
    @Contract(value = "_ -> new", pure = true)
    public static <E extends Entity> @NotNull Builder<E> builder(Class<E> entityClass) {
        return new Builder<>(entityClass);
    }

    /**
     * Represents a {@code builder} that is used to create {@link SelectorRequirements} instances
     * @param <E> The type that the selected entities must extend
     */
    public static class Builder<E extends Entity> {
        private final Class<E> entityClass;
        private int limit = Integer.MAX_VALUE;

        private Builder(Class<E> entityClass) {
            this.entityClass = entityClass;
        }

        /**
         * Sets a limit on the number of entities that can be selected
         * @param limit The limit to set
         * @return {@code this}, for chaining
         */
        public Builder<E> limit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Creates a new {@code SelectorRequirements} based on this builder
         * @return The newly created {@code SelectorRequirements}
         */
        public SelectorRequirements<E> build() {
            return new SelectorRequirements<>(this);
        }

        /**
         * Creates a builder that is used to create an instance of {@code SelectorRequirements}
         * @param entityClass The entity class that the selected entities must extend
         * @return The newly created {@code Builder}
         * @param <E> The type the selected entities must extend
         */
        @Contract(value = "_ -> new", pure = true)
        public static <E extends Entity> @NotNull Builder<E> create(Class<E> entityClass) {
            return new Builder<>(entityClass);
        }
    }
}
