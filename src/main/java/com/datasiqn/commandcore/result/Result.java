package com.datasiqn.commandcore.result;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a result of a method
 * @param <V> The type of the {@code Ok} value
 * @param <E> The type of the {@code Error} value
 */
public class Result<V, E extends Exception> {
    private final V value;
    private final E error;
    private final boolean caughtError;

    private Result(V value) {
        this.value = value;
        this.error = null;
        caughtError = false;
    }
    private Result(E error) {
        this.value = null;
        this.error = error;
        caughtError = true;
    }

    public void match(Consumer<V> okConsumer, Consumer<E> errorConsumer) {
        if (isOk()) okConsumer.accept(value);
        errorConsumer.accept(error);
    }

    public <T> T matchResult(Function<V, T> okFunction, Function<E, T> errorFunction) {
        if (isOk()) return okFunction.apply(value);
        return errorFunction.apply(error);
    }

    public boolean isOk() {
        return !caughtError;
    }

    public boolean isError() {
        return caughtError;
    }

    public V unwrap() throws UnwrapException {
        if (error == null) return value;
        throw new UnwrapException("unwrap", "err");
    }

    public E unwrapError() throws UnwrapException {
        if (isError()) return error;
        throw new UnwrapException("unwrapError", "ok");
    }

    public void ifOk(Consumer<V> consumer) {
        match(consumer, error -> {});
    }

    public void ifError(Consumer<E> consumer) {
        match(value -> {}, consumer);
    }

    public <N> Result<N, E> and(Result<N, E> result) {
        return matchResult(value -> result, Result::error);
    }

    public <N> Result<N, E> andThen(Function<V, Result<N, E>> function) {
        return matchResult(function, Result::error);
    }

    public <N extends Exception> Result<V, N> or(Result<V, N> result) {
        return matchResult(Result::ok, error -> result);
    }

    public <N extends Exception> Result<V, N> orElse(Function<E, Result<V, N>> function) {
        return matchResult(Result::ok, function);
    }

    public V unwrapOr(V defaultValue) {
        return matchResult(value -> this.value, error -> defaultValue);
    }

    public <N> Result<N, E> map(Function<V, N> mapper) {
        return matchResult(value -> ok(mapper.apply(value)), Result::error);
    }

    public <N> N mapOr(Function<V, N> mapper, N defaultValue) {
        return matchResult(mapper, error -> defaultValue);
    }

    public <N extends Exception> Result<V, N> mapError(Function<E, N> mapper) {
        return matchResult(Result::ok, error -> error(mapper.apply(error)));
    }

    @Contract(value = "_ -> new", pure = true)
    public static <V, E extends Exception> @NotNull Result<V, E> ok(V value) {
        return new Result<>(value);
    }

    @Contract(value = "_ -> new", pure = true)
    public static <V, E extends Exception> @NotNull Result<V, E> error(E error) {
        return new Result<>(error);
    }

    public static <V, E extends Exception> @NotNull Result<V, E> ofNullable(@Nullable V value, E error) {
        if (value == null) return error(error);
        return ok(value);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static <V, F extends Exception, E extends Exception> Result<V, E> resolve(ValueSupplier<V, F> supplier, Function<Exception, E> errorMapper) {
        try {
            return ok(supplier.getValue());
        } catch (Exception e) {
            return error(errorMapper.apply(e));
        }
    }

    @FunctionalInterface
    public interface ValueSupplier<T, E extends Exception> {
        T getValue() throws E;
    }
}
