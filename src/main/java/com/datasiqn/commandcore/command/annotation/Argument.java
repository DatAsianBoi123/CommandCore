package com.datasiqn.commandcore.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate an argument parameter. Every parameter after the {@code source} parameter must be annotated with this.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Argument {
    /**
     * Gets the name of the argument
     * @return The name of the argument
     */
    String name();

    /**
     * Gets whether this argument is optional or not.
     * By setting this to {@code true}, the value of this (and any subsequent) parameter has the possibility of being {@code null}.
     * @return Whether this argument is optional or not
     */
    boolean optional() default false;
}
