package com.datasiqn.commandcore.command.annotation;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.builder.LiteralBuilder;
import com.datasiqn.commandcore.command.source.CommandSource;
import com.datasiqn.commandcore.locatable.LocatableCommandSender;
import com.datasiqn.commandcore.managers.ArgumentTypeManager;
import com.datasiqn.commandcore.managers.CommandManager;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Represents a command that is created using annotations. This is the alternative to using a {@link CommandBuilder}.
 * Although using this requires less boilerplate and is less error-prone, it cannot do everything a {@link CommandBuilder} can.
 * For simpler commands, using this is sufficient. However, more complex commands may require you to use a {@link CommandBuilder}.
 * <p>
 * To register this command, you can use {@link CommandManager#registerCommand(AnnotationCommand)}.
 * You can also use {@link CommandBuilderGenerator#fromAnnotationCommand(AnnotationCommand)} to convert this into a {@link CommandBuilder}.
 * <h2>Usage</h2>
 * To create an annotation command, make a class that {@code implements} this and annotated with {@link CommandDescription}.
 * The {@link CommandDescription} contains information about the command, such as its name, description, aliases, etc.
 * <p>
 * In order for users to execute this command, {@code Executor}(s) must be used.
 * {@code Executor}s are methods in the implementing class that can be executed from a user typing the command.
 * These methods can be of any visibility, and aren't required to be {@code public}.
 * They can also have any return type.
 * The method will be invoked whenever the user
 * To create an {@code Executor}, either annotate a method with {@link Executor} or {@link LiteralExecutor}.
 * The latter prepends a {@link LiteralBuilder} onto that executor's branch.
 * <pre>
 *     {@code
 *
 *     @Executor
 *     public void executor(args) { ... }
 *     // /root command (args)
 *
 *     @LiteralExecutor("literal")
 *     public void literalExecutor(args) { ... }
 *     // /root command literal (args)
 *
 *     }
 * </pre>
 * Every {@code Executor}'s first parameter has to be the command source. The source can be any of the following types:
 * <ul>
 *     <li>{@link CommandSource}</li>
 *     <li>{@link CommandSender}</li>
 *     <li>{@link LocatableCommandSender}</li>
 *     <li>{@link BlockCommandSender}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Player}</li>
 * </ul>
 * The source will be automatically {@code require}d during parsing.
 * <p>
 * Any other parameter succeeding the source parameter will be interpreted as a command {@code Argument}, which requires the {@link Argument} annotation.
 * All {@code Argument}s must be a registered {@link ArgumentType}. Registration is done with the {@link ArgumentTypeManager}, which can be accessed by using {@link CommandCore#getArgumentTypeManager()}.
 * Almost every single built-in {@link ArgumentType} (found as static fields/methods in {@link ArgumentType}) are automatically registered.
 * Some registered {@link ArgumentType}s require an annotation to specify extra data or to differentiate between an {@link ArgumentType} of the same inner type.
 * To view specific registration details, you can look at the JavaDoc for that {@link ArgumentType}.
 * <p>
 * By default, all {@code Argument}s are required. To make them optional, you can annotate them with {@link Optional}.
 * If the user does not supply that {@code Argument}, {@code null} is passed to that parameter.
 * This will cause {@link NullPointerException}s when dealing with optional primitives, so, to avoid this issue, use the boxed version of every optional primitive.
 * <pre>
 *     <code>int</code>     -> <code>Integer</code>
 *     <code>double</code>  -> <code>Double</code>
 * </pre>
 * <strong>Note:</strong> Equal {@code Argument}s will NOT be merged into other branches.
 * For example:
 * <pre>
 *     {@code
 *
 *     @Executor
 *     public void setName(
 *         CommandSender source,
 *         @Argument(name = "entity") @Limit(1) EntitySelector<Entity> entity,
 *         @Argument(name = "name") @QuotedWord String newName
 *     ) { ... }
 *     // To execute this command, the user would type:
 *     // /root command @e[limit=1,sort=nearest] "Some Cool Name"
 *
 *     @Executor
 *     public void setHealth(
 *         CommandSender source,
 *         @Argument(name = "entity") @Limit(1) EntitySelector<Entity> entity,
 *         @Argument(name = "health") @BoundedDouble(0) double newHealth
 *     ) { ... }
 *     // This branch is IMPOSSIBLE to execute.
 *     // This is because this branch is not merged into the other executor. The first executor will always be evaluated first.
 *
 *     }
 * </pre>
 */
public interface AnnotationCommand {
}
