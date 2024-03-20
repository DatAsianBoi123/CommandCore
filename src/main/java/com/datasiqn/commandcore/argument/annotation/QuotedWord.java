package com.datasiqn.commandcore.argument.annotation;

import com.datasiqn.commandcore.argument.type.ArgumentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a {@link String} as {@link ArgumentType#QUOTED_WORD}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface QuotedWord {
}
