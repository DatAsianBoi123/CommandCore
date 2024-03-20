package com.datasiqn.commandcore.argument.annotation;

import com.datasiqn.commandcore.argument.type.ArgumentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a {@code float} as {@link ArgumentType#boundedNumber(Class, Number, Number) ArgumentType#boundedNumber}.
 * This should only annotate {@code float} parameters. If any other number type is annotated with this, it is ignored.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface BoundedFloat {
    /**
     * Gets the minimum bounds
     * @return The minimum bounds
     */
    float min();

    /**
     * Gets the maximum bounds
     * @return The maximum bounds
     */
    float max() default Float.POSITIVE_INFINITY;
}
