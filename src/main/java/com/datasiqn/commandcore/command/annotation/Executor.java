package com.datasiqn.commandcore.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a method inside an {@link AnnotationCommand} as the command executor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Executor {
    /**
     * Gets whether this executor should execute asynchronously or not
     * @return {@code true} if this executor should execute async, {@code false} otherwise
     */
    boolean async() default false;
}
