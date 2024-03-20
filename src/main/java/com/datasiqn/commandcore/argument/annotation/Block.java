package com.datasiqn.commandcore.argument.annotation;

import com.datasiqn.commandcore.argument.type.ArgumentType;
import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate a {@link Material} as {@link ArgumentType#BLOCK}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Block {
}
