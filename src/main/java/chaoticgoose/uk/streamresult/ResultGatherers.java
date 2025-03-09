package chaoticgoose.uk.streamresult;

import chaoticgoose.uk.streamresult.LambdaExceptionUtils.FunctionWithException;

import java.util.function.Function;
import java.util.stream.Gatherer;

@SuppressWarnings("preview")
public final class ResultGatherers {
    private ResultGatherers() {
    }

    public static <T, R, E extends Exception> Gatherer<T, ?, Result<R, Cause.Single<E>>> mapFallible(FunctionWithException<T, R, E> mapper, boolean continueAfterFailure) {
        class State {
            private boolean hasFailed = false;
        }

        return Gatherer.ofSequential(State::new, (state, element, downstream) -> {
            if (state.hasFailed && !continueAfterFailure) {
                return false;
            }
            var result = Result.catching(() -> mapper.apply(element), Cause.Single::of);

            if (result.isFailure()) {
                state.hasFailed = true;
            }

            return downstream.push(result);
        });
    }

    public static <T, R, E1 extends Exception, E2 extends Exception> Gatherer<Result<T, Cause.Single<E1>>, ?, Result<R, Cause.Double<E1, E2>>> mapFallible2(FunctionWithException<T, R, E2> mapper, boolean continueAfterFailure) {
        return mapWhileSuccessUpgrade(mapper, Cause.Double::fromSingle, Cause.Double::ofSecond, continueAfterFailure);
    }

    public static <T, R, E1 extends Exception, E2 extends Exception, E3 extends Exception> Gatherer<Result<T, Cause.Double<E1, E2>>, ?, Result<R, Cause.Triple<E1, E2, E3>>> mapFallible3(FunctionWithException<T, R, E3> mapper, boolean continueAfterFailure) {
        return mapWhileSuccessUpgrade(mapper, Cause.Triple::fromDouble, Cause.Triple::ofThird, continueAfterFailure);
    }

    private static <T, R, E extends Exception, C1 extends Cause, C2 extends Cause> Gatherer<Result<T, C1>, ?, Result<R, C2>> mapWhileSuccessUpgrade(FunctionWithException<T, R, E> mapper, Function<C1, C2> causeUpgrade, Function<E, C2> causeFactory, boolean continueAfterFailure) {
        class State {
            private boolean hasFailed = false;
        }

        return Gatherer.ofSequential(State::new, (state, element, downstream) -> {
            if (element.isFailure()) {
                return downstream.push(new Result.Failure<>(causeUpgrade.apply(element.failureOrThrow().cause())));
            }

            if (state.hasFailed && !continueAfterFailure) {
                return false;
            }
            Result.Success<T, C1> success = element.successOrThrow();
            Result<R, C2> result = Result.catching(() -> mapper.apply(success.value()), causeFactory);

            if (result.isFailure()) {
                state.hasFailed = true;
            }

            return downstream.push(result);
        });
    }

    public static <T, R, C extends Cause> Gatherer<Result<T, C>, ?, Result<R, C>> mapSuccesses(Function<T, R> mapper) {
        return Gatherer.ofSequential((_, result, downstream) -> {
            if (result.isFailure()) {
                Result.Failure<R, C> mappedFailure = new Result.Failure<>(result.failureOrThrow().cause());
                return downstream.push(mappedFailure);
            }

            return downstream.push(new Result.Success<>(mapper.apply(result.successOrThrow().value())));
        });
    }
}
