package com.datasiqn.commandcore.managers;

import com.datasiqn.commandcore.argument.annotation.*;
import com.datasiqn.commandcore.argument.numrange.NumberRange;
import com.datasiqn.commandcore.argument.selector.EntitySelector;
import com.datasiqn.commandcore.argument.selector.SelectorRequirements;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.annotation.AnnotationCommand;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

// TODO: write more javadocs explaining argument parameters
/**
 * Class that manages registered {@link ArgumentType}s.
 * {@link ArgumentType}s must be registered to be used by an annotation command. All builtin {@link ArgumentType}s are automatically registered once {@code CommandCore} is registered, except when stated otherwise.
 */
public class ArgumentTypeManager {
    private final Map<String, Map<String, RegisteredArgumentType<?, ?>>> registeredArgumentTypes = new HashMap<>();
    private final Multimap<String, ArgumentTypeSupplier<?, Parameter>> customRegisteredArgumentTypes = MultimapBuilder.hashKeys().arrayListValues().build();

    private boolean registeredBuiltin = false;

    /**
     * Registers a {@code SimpleArgumentType} with the required annotation of {@code null} and a type of {@code T}.
     * If the argument parameter is the same type as {@code T}, it will be mapped to {@code argumentType}.
     * @param argumentType The {@link ArgumentType} that will be used when a parameter has type {@code T}
     * @param <T> The type of {@code argumentType}
     * @throws IllegalArgumentException If the type {@code T} with no required annotation is already mapped to a different {@link ArgumentType}.
     *                                  If you want to have a type that is mapped to multiple {@link ArgumentType}s, use {@link #register(ArgumentType, Class)} instead.
     * @see ArgumentTypeManager
     */
    public <T> void register(@NotNull ArgumentType<T> argumentType) {
        register(argumentType, null);
    }

    /**
     * Registers a {@code SimpleArgumentType} with the required annotation of {@code requiredAnnotation} and a type of {@code T}.
     * If the argument parameter is the same type as {@code T} and is annotated with {@code requiredAnnotation}, it will be mapped to {@code argumentType}.
     * <p>
     * If you need annotation values to create an {@link ArgumentType}, use {@link #register(Class, Class, ArgumentTypeSupplier)} instead
     * @param argumentType The {@link ArgumentType} that will be used when a parameter has type {@code T} and is annotated with {@code requiredAnnotation}
     * @param requiredAnnotation The annotation that must be present on the argument parameter for it to be mapped to {@code argumentType}.
     *                           This is useful when multiple {@link ArgumentType}s have the same data type, and you want to map between them.
     * @param <T> The type of {@code argumentType}
     * @throws IllegalArgumentException If the type {@code T} with the required annotation of {@code requiredAnnotation} is already mapped to a different {@link ArgumentType}.
     * @see ArgumentTypeManager
     */
    public <T> void register(ArgumentType<T> argumentType, @Nullable Class<? extends Annotation> requiredAnnotation) {
        register(new RegisteredArgumentType<>(argumentType, requiredAnnotation));
    }

    /**
     * Registers a {@code SimpleArgumentType} with the required annotation of {@code requiredAnnotation} and an argument class of {@code argumentClass}.
     * If the argument parameter's class is {@code argumentClass} and is annotated with {@code requiredAnnotation}, it will be mapped to the result of {@code argumentTypeSupplier}.
     * <p>
     * This should be used when annotation values are needed to create a {@link ArgumentType}. If not, {@link #register(ArgumentType, Class)} should be used instead.
     * @param argumentClass The class of the argument
     * @param requiredAnnotation The annotation that must be present on the argument parameter for it to be mapped to the result of {@code argumentTypeSupplier}
     * @param argumentTypeSupplier A supplier that takes in the annotation and supplies the newly created {@link ArgumentType}.
     *                             A return value of {@code null} means that the {@link ArgumentType} wasn't was unable to be created from the annotation and will be ignored.
     * @param <T> The type of the argument type
     * @param <A> The type of the required annotation
     * @throws IllegalArgumentException If the clas {@code argumentClass} with the required annotation of {@code requiredAnnotation} is already mapped to a different {@link ArgumentType}.
     * @see ArgumentTypeManager
     */
    public <T, A extends Annotation> void register(@NotNull Class<T> argumentClass, @Nullable Class<A> requiredAnnotation, ArgumentTypeSupplier<T, A> argumentTypeSupplier) {
        register(new RegisteredArgumentType<>(argumentClass, requiredAnnotation, argumentTypeSupplier));
    }

    private <T, A extends Annotation> void register(@NotNull RegisteredArgumentType<T, A> registeredArgumentType) {
        String className = Primitives.wrap(registeredArgumentType.argumentClass).getName();
        Class<A> annotation = registeredArgumentType.requiredAnnotation;
        String annotationName = annotation == null ? null : annotation.getName();
        Map<String, RegisteredArgumentType<?, ?>> argumentTypeMap = registeredArgumentTypes.computeIfAbsent(className, key -> new HashMap<>());
        RegisteredArgumentType<?, ?> previousValue = argumentTypeMap.putIfAbsent(annotationName, registeredArgumentType);
        if (previousValue != null) {
            throw new IllegalArgumentException("argument type with class " + className + " and annotation " + annotationName + " already exists");
        }
    }

    /**
     * Registers a {@code CustomArgumentType} with the argument class of {@code argumentClass}.
     * <p>
     * This method should rarely be used, and in most cases, the other {@code register} methods will be sufficient. However, there are certain cases where direct access to the argument parameter is necessary.
     * The most common reason is when the {@link ArgumentType} relies on generics found on the argument type.
     * Take, for example, the {@link ArgumentType#numberRange(Class) numberRange} argument type and {@link NumberRange} class.
     * Number ranges can be of any number, but the only way to know what number class is needed to create the argument type is by looking at the generic T argument of {@link NumberRange}.
     * <pre>
     * Ex.
     *      {@code NumberRange<Short>}  -> {@code numberRange(short.class)}
     *      {@code NumberRange<Double>} -> {@code numberRange(double.class)}
     * </pre>
     * To get class with generics, you can use a {@link TypeToken}.
     * <p>
     * There can be an infinite number of {@code CustomArgumentType}s that are registered under one argument class, however, only one {@link ArgumentType} gets mapped.
     * Finding a valid {@link ArgumentType} will be done in the order that this method was called, meaning {@code CustomArgumentType}s that are registered first will be evaluated first.
     * If {@code argumentTypeSupplier} returns {@code null}, it will be ignored and the next registered {@code CustomArgumentType} will be evaluated.
     * This process will continue until a {@code CustomArgumentType} returns a non-null value.
     * <p>
     * <strong>Note:</strong> Using this method can very easily become unsafe with unchecked casts and raw types.
     * The {@link ArgumentType} returned by {@code argumentTypeSupplier} <strong>must have</strong> an argument class equal to {@code argumentClass}.
     * If this is violated, casting exceptions and other undesired behavior can occur when an {@link AnnotationCommand} gets executed.
     * @param argumentClass The class of the argument
     * @param argumentTypeSupplier A supplier that takes in the argument parameter and supplies the {@link ArgumentType}.
     * @param <T> The type of the argument type
     * @see ArgumentTypeManager
     */
    public <T> void registerCustom(Class<T> argumentClass, ArgumentTypeSupplier<T, Parameter> argumentTypeSupplier) {
        customRegisteredArgumentTypes.put(Primitives.wrap(argumentClass).getName(), argumentTypeSupplier);
    }

    /**
     * Gets the registered {@link ArgumentType} based on the argument parameter {@code parameter}.
     * @param parameter The argument parameter found on the executor method
     * @return The registered {@link ArgumentType}, or {@code null} if one wasn't found
     */
    public @Nullable ArgumentType<?> get(@NotNull Parameter parameter) {
        String className = Primitives.wrap(parameter.getType()).getName();
        Map<String, RegisteredArgumentType<?, ?>> argumentTypeMap = registeredArgumentTypes.get(className);
        RegisteredArgumentType<?, ?> registeredArgumentType = null;
        Annotation annotation = null;
        if (argumentTypeMap != null) {
            Annotation[] annotations = parameter.getAnnotations();
            for (Annotation parameterAnnotation : annotations) {
                RegisteredArgumentType<?, ?> foundType = argumentTypeMap.get(parameterAnnotation.annotationType().getName());
                if (foundType != null) {
                    registeredArgumentType = foundType;
                    annotation = parameterAnnotation;
                    break;
                }
            }
            if (registeredArgumentType == null) registeredArgumentType = argumentTypeMap.get(null);
        }
        if (registeredArgumentType == null) {
            for (ArgumentTypeSupplier<?, Parameter> supplier : customRegisteredArgumentTypes.get(className)) {
                ArgumentType<?> argumentType = supplier.get(parameter);
                if (argumentType != null) {
                    return argumentType;
                }
            }
            return null;
        }
        return registeredArgumentType.getArgumentTypeUnsafe(annotation);
    }

    /**
     * Registers all builtin {@link ArgumentType}s, unless otherwise specified.
     * Builtin {@link ArgumentType}s can be found in static fields or methods in that class.
     * Most of the time you shouldn't have to manually call this, as initializing {@code CommandCore} will automatically call this method.
     * @throws IllegalStateException If builtin {@link ArgumentType}s have already been registered
     */
    public void registerBuiltin() {
        if (registeredBuiltin) throw new IllegalStateException("builtin argument types are already registered");

        // unregistered types:
        // - ONLINE_PLAYER
        // - OFFLINE_PLAYER
        // - COMMAND
        // - json

        registerStringTypes();
        register(ArgumentType.BOOLEAN);
        register(ArgumentType.UUID);
        register(ArgumentType.NAMESPACED_KEY);
        register(ArgumentType.VECTOR);
        register(ArgumentType.WORLD);
        registerEntityTypes();
        register(ArgumentType.LOOT_TABLE);
        register(ArgumentType.RECIPE);
        registerMaterialTypes();
        register(ArgumentType.COMMAND_NAME);
        registerNumberAndBoundedTypes();
        registerNumberRanges();
        registerEntitySelectorTypes();

        registeredBuiltin = true;
    }

    private void registerStringTypes() {
        register(ArgumentType.WORD, Word.class);
        register(ArgumentType.QUOTED_WORD, QuotedWord.class);
        register(ArgumentType.NAME, Name.class);
    }

    private void registerEntityTypes() {
        register(ArgumentType.ENTITY_TYPE);
        register(ArgumentType.LIVING_ENTITY_TYPE, Living.class);
        register(ArgumentType.SPAWNABLE_ENTITY_TYPE, Spawnable.class);
    }

    private void registerMaterialTypes() {
        register(ArgumentType.MATERIAL);
        register(ArgumentType.BLOCK, Block.class);
        register(ArgumentType.ITEM, Item.class);
    }

    @SuppressWarnings({"unchecked", "UnstableApiUsage", "rawtypes"})
    private void registerEntitySelectorTypes() {
        // this looks super unsafe but i swear its safe
        registerCustomWithGenerics((Class<EntitySelector<? extends Entity>>) new TypeToken<EntitySelector<? extends Entity>>() { }.getRawType(), (parameter, generics) -> {
            if (generics.length == 0 || !(generics[0] instanceof Class<?> entityClass)) return null;
            Limit limitAnnotation = parameter.getAnnotation(Limit.class);
            if (limitAnnotation == null) return null;
            SelectorRequirements requirements = SelectorRequirements.builder((Class<? extends Entity>) entityClass).limit(limitAnnotation.value()).build();
            return ArgumentType.entitySelector(requirements);
        });
    }

    private void registerNumberAndBoundedTypes() {
        forEachNumber(numClass -> register(ArgumentType.number(numClass)));

        register(byte.class, BoundedByte.class, annotation -> ArgumentType.boundedNumber(byte.class, annotation.min(), annotation.max()));
        register(short.class, BoundedShort.class, annotation -> ArgumentType.boundedNumber(short.class, annotation.min(), annotation.max()));
        register(int.class, BoundedInt.class, annotation -> ArgumentType.boundedNumber(int.class, annotation.min(), annotation.max()));
        register(long.class, BoundedLong.class, annotation -> ArgumentType.boundedNumber(long.class, annotation.min(), annotation.max()));
        register(float.class, BoundedFloat.class, annotation -> ArgumentType.boundedNumber(float.class, annotation.min(), annotation.max()));
        register(double.class, BoundedDouble.class, annotation -> ArgumentType.boundedNumber(double.class, annotation.min(), annotation.max()));
    }

    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    private void registerNumberRanges() {
        registerCustomWithFirstGeneric((Class<NumberRange<Byte>>) new TypeToken<NumberRange<Byte>>() { }.getRawType(), Byte.class, ArgumentType.numberRange(byte.class));
        registerCustomWithFirstGeneric((Class<NumberRange<Short>>) new TypeToken<NumberRange<Short>>() { }.getRawType(), Short.class, ArgumentType.numberRange(short.class));
        registerCustomWithFirstGeneric((Class<NumberRange<Integer>>) new TypeToken<NumberRange<Integer>>() { }.getRawType(), Integer.class, ArgumentType.numberRange(int.class));
        registerCustomWithFirstGeneric((Class<NumberRange<Long>>) new TypeToken<NumberRange<Long>>() { }.getRawType(), Long.class, ArgumentType.numberRange(long.class));
        registerCustomWithFirstGeneric((Class<NumberRange<Float>>) new TypeToken<NumberRange<Float>>() { }.getRawType(), Float.class, ArgumentType.numberRange(float.class));
        registerCustomWithFirstGeneric((Class<NumberRange<Double>>) new TypeToken<NumberRange<Double>>() { }.getRawType(), Double.class, ArgumentType.numberRange(double.class));
    }

    private <T> void registerCustomWithGenerics(Class<T> typeClass, BiFunction<Parameter, Type[], ArgumentType<T>> argumentTypeSupplier) {
        registerCustom(typeClass, parameter -> {
            Type[] generics;
            if (!(parameter.getParameterizedType() instanceof ParameterizedType type)) generics = new Type[0];
            else generics = type.getActualTypeArguments();
            return argumentTypeSupplier.apply(parameter, generics);
        });
    }

    private <T> void registerCustomWithFirstGeneric(Class<T> typeClass, Class<?> first, ArgumentType<T> argumentType) {
        registerCustomWithGenerics(typeClass, (clazz, generics) -> {
            if (generics.length == 0) return null;
            return generics[0] == first ? argumentType : null;
        });
    }

    private static void forEachNumber(@NotNull Consumer<Class<? extends Number>> consumer) {
        consumer.accept(byte.class);
        consumer.accept(short.class);
        consumer.accept(int.class);
        consumer.accept(long.class);
        consumer.accept(float.class);
        consumer.accept(double.class);
    }

    /**
     * A supplier that supplies an {@link ArgumentType} based on other data of type {@code S}
     * @param <T> The type of the argument type
     * @param <S> Extra data that can be used when creating the {@link ArgumentType}. This is most commonly an annotation.
     */
    @FunctionalInterface
    public interface ArgumentTypeSupplier<T, S> {
        /**
         * Creates the {@link ArgumentType} based on {@code s}
         * @param s The extra data
         * @return The {@link ArgumentType}, or {@code null} if one was unable to be created
         */
        @Nullable ArgumentType<T> get(S s);
    }

    private static class RegisteredArgumentType<T, A extends Annotation> {
        private final Class<T> argumentClass;
        private final Class<A> requiredAnnotation;
        private final ArgumentTypeSupplier<T, A> argumentTypeSupplier;
        private final ArgumentType<T> argumentType;

        public RegisteredArgumentType(@NotNull ArgumentType<T> argumentType, Class<A> requiredAnnotation) {
            this.argumentClass = argumentType.getArgumentClass();
            this.requiredAnnotation = requiredAnnotation;
            this.argumentType = argumentType;

            this.argumentTypeSupplier = null;
        }
        public RegisteredArgumentType(Class<T> argumentClass, Class<A> requiredAnnotation, ArgumentTypeSupplier<T, A> argumentTypeSupplier) {
            this.argumentClass = argumentClass;
            this.requiredAnnotation = requiredAnnotation;
            this.argumentTypeSupplier = argumentTypeSupplier;

            this.argumentType = null;
        }

        public ArgumentType<T> getArgumentTypeUnsafe(Annotation annotation) {
            if (argumentType == null) {
                //noinspection unchecked
                return argumentTypeSupplier.get((A) annotation);
            } else {
                return argumentType;
            }
        }
    }
}
