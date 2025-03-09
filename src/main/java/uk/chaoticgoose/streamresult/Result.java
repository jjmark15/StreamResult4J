package uk.chaoticgoose.streamresult;

import uk.chaoticgoose.streamresult.LambdaExceptionUtils.SupplierWithException;

import java.util.function.Function;

public sealed interface Result<V, C extends Cause> {
    static <T, E extends Exception, C extends Cause> Result<T, C> catching(SupplierWithException<T, E> supplier, Function<E, C> causeFunction) {
        try {
            return new Success<>(supplier.get());
        } catch (Exception e) {
            return new Failure<>(causeFunction.apply(safeCast(e)));
        }
    }

    default V valueOrThrow() {
        return (switch (this) {
            case Result.Success<V, C> success -> success;
            case Result.Failure<?, ?> _ -> throw new IllegalStateException("Result is not a Success");
        }).value();
    }

    default C causeOrThrow() {
        return (switch (this) {
            case Result.Success<?, ?> _ -> throw new IllegalStateException("Result is not a Failure");
            case Result.Failure<V, C> failure -> failure;
        }).cause();
    }

    default boolean isFailure() {
        return switch (this) {
            case Success<?, ?> _ -> false;
            case Failure<?, ?> _ -> true;
        };
    }

    default <T> Result<T, C> mapSuccess(Function<V, T> mapper) {
        return switch (this) {
            case Success<V, C> success -> new Success<>(mapper.apply(success.value));
            case Result.Failure<V, C> failure -> new Failure<>(failure.cause);
        };
    }

    record Success<S, C extends Cause>(S value) implements Result<S, C> {
    }

    record Failure<S, C extends Cause>(C cause) implements Result<S, C> {
    }

    @SuppressWarnings("unchecked")
    private static <E extends Exception> E safeCast(Exception e) {
        try {
            return (E) e;
        } catch (ClassCastException cce) {
            throw new RuntimeException(e);
        }
    }
}
