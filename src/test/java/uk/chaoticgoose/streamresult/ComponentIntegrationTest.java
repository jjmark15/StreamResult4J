package uk.chaoticgoose.streamresult;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import uk.chaoticgoose.streamresult.StreamResult.Failure;
import uk.chaoticgoose.streamresult.StreamResult.Success;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static uk.chaoticgoose.streamresult.ResultCollectors.toResultList;
import static uk.chaoticgoose.streamresult.ResultCollectors.toSingleResult;
import static uk.chaoticgoose.streamresult.ResultGatherers.FailureAction.Continue;
import static uk.chaoticgoose.streamresult.ResultGatherers.FailureAction.Stop;
import static uk.chaoticgoose.streamresult.ResultGatherers.*;

public class ComponentIntegrationTest {
    @Test
    void mapsSuccessesWithFallibleOperation() {
        StreamResult<List<Integer>, Cause.@NonNull Single<@NonNull SomeException>> result = Stream.of(0, 1, 2)
            .gather(mapFallible(this::throwsIfLessThanZero, Stop))
            .gather(mapSuccesses(n -> n - 4))
            .collect(toSingleResult());

        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.success().map(Success::value)).hasValue(List.of(-4, -3, -2));
    }

    @Test
    void doesNotProcessAnyMoreStreamElementsInMapFallibleIfFailureActionIsStop() {
        ResultList<Integer, Cause.@NonNull Single<@NonNull SomeException>> result = Stream.of(1, 0, -1, -2)
            .gather(mapFallible(this::throwsIfLessThanZero, Stop))
            .gather(mapSuccesses(n -> {
                if (n < 0) {
                    throw new RuntimeException();
                }
                return n;
            }))
            .collect(toResultList());

        assertThat(result.successValues()).containsExactly(1, 0);
    }

    @Test
    void continuesProcessingStreamElementsInMapFallibleIfFailureActionIsContinue() {
        ResultList<Integer, Cause.@NonNull Single<@NonNull SomeException>> result = Stream.of(1, 0, -1, 2)
            .gather(mapFallible(this::throwsIfLessThanZero, Continue))
            .gather(mapSuccesses(n -> {
                if (n < 0) {
                    throw new RuntimeException();
                }
                return n;
            }))
            .collect(toResultList());

        assertThat(result.successValues()).containsExactly(1, 0, 2);
    }

    @Test
    void mapsFailuresAndSuccessesWithMultipleFallibleOperations() {
        ResultList<Integer, Cause.@NonNull Double<@NonNull SomeException, @NonNull OtherException>> results = Stream.of(-1, 0, 1)
            .gather(mapFallible(this::throwsIfLessThanZero, Continue))
            .gather(mapFallible2(this::throwsIfLessThanOne, Continue))
            .collect(toResultList());

        assertThat(results.successValues()).containsExactly(1);
        assertThat(results.failureCauses())
            .extracting(Cause.Double::exception)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new SomeException(), new OtherException());
    }

    @Test
    void throwsEachExceptionCause() {
        StreamResult<List<Integer>, Cause.@NonNull Triple<@NonNull SomeException, @NonNull OtherException, @NonNull YetAnotherException>> result = Stream.of(-1, 0, 1)
            .gather(mapFallible(this::throwsIfLessThanZero, Continue))
            .gather(mapFallible2(this::throwsIfLessThanOne, Continue))
            .gather(mapFallible3(this::throwsIfEqualToOne, Continue))
            .collect(toSingleResult());

        try {
            switch (result) {
                case Failure<?, Cause.@NonNull Triple<@NonNull SomeException, @NonNull OtherException, @NonNull YetAnotherException>> failure ->
                    failure.cause().throwExceptions();
                case Success<List<Integer>, ?> _ -> throw new RuntimeException();
            }
        } catch (OtherException | SomeException | YetAnotherException e) {
            // got here
            return;
        }
        fail("Should have thrown an exception");
    }

    @Test
    void mapsSuccessesWithFallibleOperation_mapper() {
        StreamResult<List<Integer>, Cause.@NonNull Triple<@NonNull SomeException, @NonNull OtherException, @NonNull YetAnotherException>> result = Stream.of(0, 1, 2)
            .map(ResultMappers.mapFallible(this::throwsIfLessThanZero))
            .map(ResultMappers.mapFallible2(this::throwsIfLessThanOne))
            .map(ResultMappers.mapFallible3(this::throwsIfEqualToOne))
            .map(ResultMappers.mapSuccesses(i -> i + 1))
            .collect(toSingleResult());

        try {
            switch (result) {
                case Failure<?, Cause.@NonNull Triple<@NonNull SomeException, @NonNull OtherException, @NonNull YetAnotherException>> failure ->
                    failure.cause().throwExceptions();
                case Success<List<Integer>, ?> _ -> throw new RuntimeException();
            }
        } catch (OtherException | SomeException | YetAnotherException e) {
            // got here
            return;
        }
        fail("Should have thrown an exception");
    }

    private Integer throwsIfLessThanZero(Integer i) throws SomeException {
        if (i < 0) {
            throw new SomeException();
        }
        return i;
    }

    private Integer throwsIfLessThanOne(Integer i) throws OtherException {
        if (i < 1) {
            throw new OtherException();
        }
        return i;
    }

    private Integer throwsIfEqualToOne(Integer i) throws YetAnotherException {
        if (i == 1) {
            throw new YetAnotherException();
        }
        return i;
    }

    private static class SomeException extends Exception {}

    private static class OtherException extends Exception {}

    private static class YetAnotherException extends Exception {}
}
