package com.datasiqn.commandcore.argument.annotation;

import com.datasiqn.commandcore.argument.type.ArgumentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a {@code byte} as {@link ArgumentType#boundedNumber(Class, Number, Number) ArgumentType#boundedNumber}.
 * This should only annotate {@code byte} parameters. If any other number type is annotated with this, it is ignored.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface BoundedByte {
    /**
     * Gets the minimum bounds
     * @return The minimum bounds
     */
    byte min();

    /**
     * Gets the maximum bounds
     * @return The maximum bounds
     */
    byte max() default Byte.MAX_VALUE;
}
