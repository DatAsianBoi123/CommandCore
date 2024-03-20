package com.datasiqn.commandcore.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate an argument parameter as optional and not required.
 * By annotating a parameter with this, the value of this (and any subsequent) parameter have the possibility of being {@code null}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Optional {
}
