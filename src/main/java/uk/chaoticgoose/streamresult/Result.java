package uk.chaoticgoose.streamresult;

import uk.chaoticgoose.streamresult.LambdaExceptionUtils.SupplierWithException;

import java.util.function.Function;

public sealed interface Result<S, F extends Cause> {
    static <T, E extends Exception, C extends Cause> Result<T, C> catching(SupplierWithException<T, E> supplier, Function<E, C> causeFunction) {
        try {
            return new Success<>(supplier.get());
        } catch (Exception e) {
            return new Failure<>(causeFunction.apply(safeCast(e)));
        }
    }

    default Success<S, F> successOrThrow() {
        throw new IllegalStateException("Result is not a Success");
    }

    default Failure<S, F> failureOrThrow() {
        throw new IllegalStateException("Result is not a Failure");
    }

    default boolean isFailure() {
        return false;
    }

    default <T> Result<T, F> mapSuccess(Function<S, T> mapper) {
        return switch (this) {
            case Success<S, F> success -> new Success<>(mapper.apply(success.value));
            case Result.Failure<S, F> failure -> new Failure<>(failure.cause);
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
