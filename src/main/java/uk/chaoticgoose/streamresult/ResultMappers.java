package uk.chaoticgoose.streamresult;

import uk.chaoticgoose.streamresult.Cause.Double;
import uk.chaoticgoose.streamresult.LambdaExceptionUtils.FunctionWithException;

import java.util.function.Function;

import static uk.chaoticgoose.streamresult.Cause.Double.*;
import static uk.chaoticgoose.streamresult.Cause.Triple.fromDouble;
import static uk.chaoticgoose.streamresult.StreamResult.catching;

public final class ResultMappers {
    private ResultMappers() {
    }

    public static <U, V, E extends Exception> Function<U, StreamResult<V, Single<E>>> mapFallible(FunctionWithException<U, V, E> function) {
        return in -> catching(() -> function.apply(in), Single::of);
    }

    public static <U, V, E1 extends Exception, E2 extends Exception> Function<StreamResult<U, Single<E1>>, StreamResult<V, Double<E1, E2>>>
    mapFallible2(FunctionWithException<U, V, E2> function) {
        return in -> {
            if (in.isFailure()) {
                return new StreamResult.Failure<>(fromSingle(in.causeOrThrow()));
            }
            return catching(() -> function.apply(in.valueOrThrow()), Double::ofSecond);
        };
    }

    public static <U, V, E1 extends Exception, E2 extends Exception, E3 extends Exception> Function<StreamResult<U, Double<E1, E2>>, StreamResult<V, Triple<E1, E2, E3>>>
    mapFallible3(FunctionWithException<U, V, E3> function) {
        return in -> {
            if (in.isFailure()) {
                return new StreamResult.Failure<>(fromDouble(in.causeOrThrow()));
            }
            return catching(() -> function.apply(in.valueOrThrow()), Triple::ofThird);
        };
    }

    public static <U, V, C extends Cause> Function<StreamResult<U, C>, StreamResult<V, C>>
    mapSuccesses(Function<U, V> function) {
        return in -> in.mapSuccess(function);
    }
}
