package com.datasiqn.commandcore.argument.annotation;

import com.datasiqn.commandcore.argument.selector.EntitySelector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to define an {@link EntitySelector}'s limit. Required for {@link EntitySelector} argument parameters.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Limit {
    /**
     * Gets the limit
     * @return The limit
     */
    int value() default Integer.MAX_VALUE;
}
