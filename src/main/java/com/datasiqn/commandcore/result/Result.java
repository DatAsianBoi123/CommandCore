package com.datasiqn.commandcore.result;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

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

    public boolean isOk() {
        return !caughtError;
    }

    public boolean isError() {
        return caughtError;
    }

    public Result<V, E> computeIfOk(Consumer<V> consumer) {
        if (isOk()) consumer.accept(value);
        return this;
    }

    public Result<V, E> computeIfError(Consumer<E> consumer) {
        if (isError()) consumer.accept(error);
        return this;
    }

    public Result<V, E> and(Function<V, Result<V, E>> function) {
        if (isOk()) return function.apply(value);
        return error(error);
    }

    public Result<V, E> or(Result<V, E> result) {
        if (isOk()) return ok(value);
        return result;
    }

    public V orElse(V value) {
        if (isOk()) return this.value;
        return value;
    }

    public V unwrap() throws UnwrapException {
        if (error == null) return value;
        throw new UnwrapException("unwrap", "err");
    }

    public E unwrapError() throws UnwrapException {
        if (isError()) return error;
        throw new UnwrapException("unwrapError", "ok");
    }

    public <N> Result<N, E> map(Function<V, N> mapper) {
        if (isOk()) return ok(mapper.apply(value));
        return error(error);
    }

    public <N> N mapOr(Function<V, N> mapper, N defaultValue) {
        if (isOk()) return mapper.apply(value);
        return defaultValue;
    }

    public <N extends Exception> Result<V, N> mapError(Function<E, N> mapper) {
        if (isOk()) return ok(value);
        return error(mapper.apply(error));
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
