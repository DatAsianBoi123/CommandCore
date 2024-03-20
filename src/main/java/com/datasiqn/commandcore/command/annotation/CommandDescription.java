package com.datasiqn.commandcore.command.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a {@link AnnotationCommand} and is required to register it
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandDescription {
    /**
     * Gets the name of the command
     * @return The name of the command
     */
    @NotNull String name();

    /**
     * Gets the description of the command
     * @return The description of the command. A blank string is returned if there is no description.
     */
    @NotNull String description() default "";

    /**
     * Gets the aliases of the command
     * @return The aliases of the command
     */
    @NotNull String @NotNull [] aliases() default {};

    /**
     * Gets the permission of the command
     * @return The permission of the command. A blank string is returned when this command doesn't have a required permission
     */
    @NotNull String permission() default "";
}
