package com.datasiqn.commandcore.command.annotation;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.builder.CommandLink;
import com.datasiqn.commandcore.command.builder.LiteralBuilder;
import com.datasiqn.commandcore.command.source.CommandSource;
import com.datasiqn.commandcore.locatable.LocatableCommandSender;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Utility class that is used to convert an {@link AnnotationCommand} to a {@link CommandBuilder}
 */
public final class CommandBuilderGenerator {
    private CommandBuilderGenerator() {
        throw new IllegalStateException("cannot instantiate util class");
    }

    /**
     * Generates a {@code CommandBuilder} from the {@code AnnotationCommand} {@code command}
     * @param command The annotation command to use
     * @return A result with an {@code Ok} value of the generated {@link CommandBuilder}, and an {@code Error} value of the error message
     */
    public static Result<CommandBuilder, FromAnnotationCommandError> fromAnnotationCommand(@NotNull AnnotationCommand command) {
        Class<? extends AnnotationCommand> commandClass = command.getClass();
        CommandDescription commandDescription = commandClass.getAnnotation(CommandDescription.class);
        if (commandDescription == null) {
            return Result.error(FromAnnotationCommandError.fromContext(commandClass, "Annotation command must be annotated with @CommandDescription"));
        }

        CommandBuilder commandBuilder = new CommandBuilder(commandDescription.name());

        String description = commandDescription.description();
        if (!description.isEmpty()) commandBuilder.description(description);

        commandBuilder.alias(commandDescription.aliases());

        String permission = commandDescription.permission();
        if (!permission.isEmpty()) commandBuilder.permission(permission);

        Method executeMethod = null;
        List<LiteralMethod> literalExecutors = new ArrayList<>();
        for (Method method : commandClass.getDeclaredMethods()) {
            LiteralExecutor literalExecutor = method.getAnnotation(LiteralExecutor.class);
            boolean isExecutor = method.isAnnotationPresent(Executor.class);

            if (!isExecutor && literalExecutor == null) continue;

            if (method.getParameterCount() == 0) {
                return Result.error(FromAnnotationCommandError.fromContext(method, "Executor must have at least 1 parameter"));
            }
            if (isExecutor) {
                if (executeMethod != null) return Result.error(FromAnnotationCommandError.fromContext(commandClass, "Annotation command cannot have multiple executors annotated with @Executor"));
                executeMethod = method;
            }
            if (literalExecutor != null) literalExecutors.add(new LiteralMethod(literalExecutor, method));
        }
        if (executeMethod == null && literalExecutors.isEmpty()) {
            Bukkit.getLogger().warning("[CommandCore] annotation command " + commandClass.getName() + " does not contain any executor methods");
            return Result.ok(commandBuilder);
        }
        if (executeMethod != null) {
            executeMethod.setAccessible(true);
            Result<None, String> buildResult = buildBranch(command, executeMethod, commandBuilder, 0);
            if (buildResult.isError()) {
                return Result.error(FromAnnotationCommandError.fromContext(executeMethod, buildResult.unwrapError()));
            }
        }
        for (LiteralMethod executor : literalExecutors) {
            Method method = executor.method;
            method.setAccessible(true);
            LiteralBuilder literal = LiteralBuilder.literal(executor.literalExecutor.value());
            Result<None, String> buildResult = buildBranch(command, method, literal, 1);
            if (buildResult.isError()) {
                return Result.error(FromAnnotationCommandError.fromContext(method, buildResult.unwrapError()));
            }
            commandBuilder.then(literal);
        }

        return Result.ok(commandBuilder);
    }

    private static Result<None, String> buildBranch(AnnotationCommand command, @NotNull Method method, CommandLink<?> builder, int offset) {
        Parameter[] parameters = method.getParameters();
        Class<?> sourceClass = parameters[0].getType();
        ArgumentType<?>[] argumentTypes = new ArgumentType[parameters.length - 1];

        CommandLink<?> link = builder;
        for (int i = 1; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            Argument argument = parameter.getAnnotation(Argument.class);
            if (argument == null) return Result.error("Executor has a parameter not annotated with @Argument");

            ArgumentType<?> argumentType = CommandCore.getInstance().getArgumentTypeManager().get(parameter);
            if (argumentType == null) {
                return Result.error("Executor has an invalid argument type " + parameter.getType().getName() + " (is it registered?)");
            }
            if (parameter.isAnnotationPresent(Optional.class)) {
                Result<None, String> addExecutorResult = addExecutor(link, sourceClass, Arrays.copyOf(argumentTypes, argumentTypes.length), method, command, offset);
                if (addExecutorResult.isError()) return addExecutorResult;
            }
            argumentTypes[i - 1] = argumentType;
            ArgumentBuilder<?> node = ArgumentBuilder.argument(argumentType, argument.name());

            link.then(node);
            link = node;
        }

        return addExecutor(link, sourceClass, argumentTypes, method, command, offset);
    }

    private static <T> @NotNull Result<None, String> addExecutor(@NotNull CommandLink<?> link, @NotNull Class<T> sourceClass, @Nullable ArgumentType<?> @NotNull [] argumentTypes, Method method, AnnotationCommand command, int offset) {
        Result<Function<CommandSource, T>, String> requireResult = requireClass(sourceClass, link);
        if (requireResult.isError()) return requireResult.and(Result.ok()).mapError(err -> "Executor has an invalid source type " + sourceClass.getName());
        Function<CommandSource, T> getSourceFunction = requireResult.unwrap();
        link.executes((context, source, arguments) -> {
            Object[] args = new Object[method.getParameterCount()];
            args[0] = getSourceFunction.apply(source);
            for (int i = 0; i < argumentTypes.length; i++) {
                if (argumentTypes[i] == null) continue;
                args[i + 1] = arguments.get(i + offset, argumentTypes[i]);
            }
            try {
                method.invoke(command, args);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            } catch (IllegalAccessException e) {
                Bukkit.getLogger().severe("[CommandCore] execute method cannot be accessed. Please report this!");
            }
        });
        return Result.ok();
    }

    @SuppressWarnings("unchecked")
    private static <T> Result<Function<CommandSource, T>, String> requireClass(Class<T> sourceClass, CommandLink<?> link) {
        if (sourceClass == CommandSource.class) return Result.ok(source -> (T) source);
        if (sourceClass == Player.class) {
            link.requiresPlayer();
            return Result.ok(source -> (T) source.getPlayer());
        }
        if (sourceClass == Entity.class) {
            link.requiresEntity();
            return Result.ok(source -> (T) source.getEntity());
        }
        if (sourceClass == BlockCommandSender.class) {
            link.requiresBlock();
            return Result.ok(source -> (T) source.getBlock());
        }
        if (sourceClass == LocatableCommandSender.class) {
            link.requiresLocatable();
            return Result.ok(source -> (T) source.getLocatable());
        }
        if (sourceClass == CommandSender.class) {
            return Result.ok(source -> (T) source.getSender());
        }
        return Result.error("class " + sourceClass.getName() + " is invalid");
    }

    private record LiteralMethod(LiteralExecutor literalExecutor, Method method) { }

    /**
     * Represents an error that can occur from generating a {@link CommandBuilder} from a {@link AnnotationCommand}
     */
    public static class FromAnnotationCommandError {
        private final Class<?> commandClass;
        private final @Nullable Method method;
        private final String reason;

        private FromAnnotationCommandError(Class<?> commandClass, @Nullable Method method, String reason) {
            this.commandClass = commandClass;
            this.method = method;
            this.reason = reason;
        }

        /**
         * Gets the message of the error
         * @return The message
         */
        public String getMessage() {
            String label;
            if (method == null) {
                label = "Error occurred when generating a CommandBuilder from annotation command " + commandClass.getName() + ": ";
            } else {
                label = "Error occurred when parsing executor " + method.getName() + " (in annotation command " + commandClass.getName() + ": ";
            }
            return label + reason;
        }

        /**
         * Creates an error from the parent annotation class and a reason
         * @param commandClass The annotation command class that generated the error
         * @param reason The reason for the error
         * @return The newly created {@code FromAnnotationCommandError}
         */
        @Contract(value = "_, _ -> new", pure = true)
        public static @NotNull FromAnnotationCommandError fromContext(Class<?> commandClass, String reason) {
            return new FromAnnotationCommandError(commandClass, null, reason);
        }
        /**
         * Creates an error from an executor method and a reason
         * @param method The executor method that generated the error
         * @param reason The reason for the error
         * @return The newly created {@code FromAnnotationCommandError}
         */
        @Contract(value = "_, _ -> new", pure = true)
        public static @NotNull FromAnnotationCommandError fromContext(Method method, String reason) {
            return new FromAnnotationCommandError(method.getDeclaringClass(), method, reason);
        }
    }
}
