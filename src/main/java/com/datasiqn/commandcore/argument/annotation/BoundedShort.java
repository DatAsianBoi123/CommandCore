package com.datasiqn.commandcore.argument.annotation;

import com.datasiqn.commandcore.argument.type.ArgumentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a {@code short} as {@link ArgumentType#boundedNumber(Class, Number, Number) ArgumentType#boundedNumber}.
 * This should only annotate {@code short} parameters. If any other number type is annotated with this, it is ignored.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface BoundedShort {
    /**
     * Gets the minimum bounds
     * @return The minimum bounds
     */
    short min();

    /**
     * Gets the maximum bounds
     * @return The maximum bounds
     */
    short max() default Short.MAX_VALUE;
}
