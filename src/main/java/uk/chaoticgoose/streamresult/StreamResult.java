package uk.chaoticgoose.streamresult;

import uk.chaoticgoose.streamresult.LambdaExceptionUtils.SupplierWithException;

import java.util.Optional;
import java.util.function.Function;

public sealed interface StreamResult<V, C extends Cause> {
    static <T, E extends Exception, C extends Cause> StreamResult<T, C> catching(SupplierWithException<T, E> supplier, Function<E, C> causeFunction) {
        try {
            return new Success<>(supplier.get());
        } catch (Exception e) {
            return new Failure<>(causeFunction.apply(safeCast(e)));
        }
    }

    default Optional<Success<V, C>> success() {
        return switch (this) {
            case StreamResult.Failure<?, ?> _ -> Optional.empty();
            case StreamResult.Success<V, C> success -> Optional.of(success);
        };
    }

    default Optional<Failure<V, C>> failure() {
        return switch (this) {
            case StreamResult.Failure<V, C> failure -> Optional.of(failure);
            case StreamResult.Success<?, ?> _ -> Optional.empty();
        };
    }

    default V valueOrThrow() {
        return (switch (this) {
            case StreamResult.Success<V, C> success -> success;
            case StreamResult.Failure<?, ?> _ -> throw new IllegalStateException("Result is not a Success");
        }).value();
    }

    default C causeOrThrow() {
        return (switch (this) {
            case StreamResult.Success<?, ?> _ -> throw new IllegalStateException("Result is not a Failure");
            case StreamResult.Failure<V, C> failure -> failure;
        }).cause();
    }

    default boolean isFailure() {
        return switch (this) {
            case Success<?, ?> _ -> false;
            case Failure<?, ?> _ -> true;
        };
    }

    default boolean isSuccess() {
        return !isFailure();
    }

    default <T> StreamResult<T, C> mapSuccess(Function<V, T> mapper) {
        return switch (this) {
            case Success<V, C> success -> new Success<>(mapper.apply(success.value));
            case StreamResult.Failure<V, C> failure -> new Failure<>(failure.cause);
        };
    }

    record Success<S, C extends Cause>(S value) implements StreamResult<S, C> {
    }

    record Failure<S, C extends Cause>(C cause) implements StreamResult<S, C> {
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
