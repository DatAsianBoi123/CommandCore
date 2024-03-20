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
}
