package com.datasiqn.commandcore.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a method inside an {@link AnnotationCommand} as a command executor with a literal as its 1st argument
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LiteralExecutor {
    /**
     * The literal's label to use
     * @return The literal's label
     */
    String value();
}
