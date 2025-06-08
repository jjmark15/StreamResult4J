package uk.chaoticgoose.streamresult;

import org.jspecify.annotations.NullMarked;
import uk.chaoticgoose.streamresult.LambdaExceptionUtils.FunctionWithException;
import uk.chaoticgoose.streamresult.StreamResult.Failure;

import java.util.function.Function;
import java.util.stream.Gatherer;

@NullMarked
public final class ResultGatherers {
    private ResultGatherers() {
    }

    public static <T, R, E extends Exception>
    Gatherer<T, ?, StreamResult<R, Cause.Single<E>>> mapFallible(FunctionWithException<T, R, E> mapper, FailureAction failureAction) {
        @NullMarked
        class State {
            private boolean hasFailed = false;
        }

        return Gatherer.ofSequential(State::new, (state, element, downstream) -> {
            if (state.hasFailed && FailureAction.Stop == failureAction) {
                return false;
            }
            var result = StreamResult.catching(() -> mapper.apply(element), Cause.Single::of);

            if (result.isFailure()) {
                state.hasFailed = true;
            }

            return downstream.push(result);
        });
    }

    public static <T, R, E1 extends Exception, E2 extends Exception>
    Gatherer<StreamResult<T, Cause.Single<E1>>, ?, StreamResult<R, Cause.Double<E1, E2>>> mapFallible2(
        FunctionWithException<T, R, E2> mapper,
        FailureAction failureAction
    ) {
        return mapFallibleN(mapper, Cause.Double::fromSingle, Cause.Double::ofSecond, failureAction);
    }

    public static <T, R, E1 extends Exception, E2 extends Exception, E3 extends Exception>
    Gatherer<StreamResult<T, Cause.Double<E1, E2>>, ?, StreamResult<R, Cause.Triple<E1, E2, E3>>> mapFallible3(
        FunctionWithException<T, R, E3> mapper,
        FailureAction failureAction
    ) {
        return mapFallibleN(mapper, Cause.Triple::fromDouble, Cause.Triple::ofThird, failureAction);
    }

    private static <T, R, E extends Exception, C1 extends Cause, C2 extends Cause>
    Gatherer<StreamResult<T, C1>, ?, StreamResult<R, C2>> mapFallibleN(
        FunctionWithException<T, R, E> mapper,
        Function<C1, C2> causeUpgrade,
        Function<E, C2> causeFactory,
        FailureAction failureAction
    ) {
        @NullMarked
        class State {
            private boolean hasFailed = false;
        }

        return Gatherer.ofSequential(State::new, (state, previousResult, downstream) -> {
            if (previousResult.isFailure()) {
                return downstream.push(new Failure<>(causeUpgrade.apply(previousResult.causeOrThrow())));
            }

            if (state.hasFailed && FailureAction.Stop == failureAction) {
                return false;
            }
            StreamResult<R, C2> result = StreamResult.catching(() -> mapper.apply(previousResult.valueOrThrow()), causeFactory);

            if (result.isFailure()) {
                state.hasFailed = true;
            }

            return downstream.push(result);
        });
    }

    public static <T, R, C extends Cause> Gatherer<StreamResult<T, C>, ?, StreamResult<R, C>> mapSuccesses(Function<T, R> mapper) {
        return Gatherer.ofSequential((_, result, downstream) -> downstream.push(result.mapSuccess(mapper)));
    }

    @NullMarked
    public enum FailureAction {
        Stop, Continue
    }
}
