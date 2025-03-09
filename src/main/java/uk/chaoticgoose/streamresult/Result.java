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

    default Success<V, C> successOrThrow() {
        throw new IllegalStateException("Result is not a Success");
    }

    default Failure<V, C> failureOrThrow() {
        throw new IllegalStateException("Result is not a Failure");
    }

    default boolean isFailure() {
        return false;
    }

    default <T> Result<T, C> mapSuccess(Function<V, T> mapper) {
        return switch (this) {
            case Success<V, C> success -> new Success<>(mapper.apply(success.value));
            case Result.Failure<V, C> failure -> new Failure<>(failure.cause);
        };
    }

    record Success<S, C extends Cause>(S value) implements Result<S, C> {
        @Override
        public Success<S, C> successOrThrow() {
            return this;
        }
    }

    record Failure<S, C extends Cause>(C cause) implements Result<S, C> {
        @Override
        public Failure<S, C> failureOrThrow() {
            return this;
        }

        @Override
        public boolean isFailure() {
            return true;
        }
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
