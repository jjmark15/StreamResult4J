package uk.chaoticgoose.streamresult;

import org.junit.jupiter.api.Test;
import uk.chaoticgoose.streamresult.StreamResult.Success;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.chaoticgoose.streamresult.ResultCollectors.toResultList;
import static uk.chaoticgoose.streamresult.ResultCollectors.toSingleResult;
import static uk.chaoticgoose.streamresult.ResultGatherers.*;

@SuppressWarnings("preview")
public class ComponentIntegrationTest {
    @Test
    void mapsSuccessesWithFallibleOperation() {
        StreamResult<List<Integer>, Cause.Single<SomeException>> result = Stream.of(0, 1, 2)
            .gather(mapFallible(this::throwsIfLessThanZero, false))
            .gather(mapSuccesses(n -> n - 4))
            .collect(toSingleResult());

        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.success().map(Success::value)).hasValue(List.of(-4, -3, -2));
    }

    @Test
    void mapsFailuresAndSuccessesWithMultipleFallibleOperations() {
        ResultList<Integer, Cause.Double<SomeException, OtherException>> results = Stream.of(-1, 0, 1)
            .gather(mapFallible(this::throwsIfLessThanZero, true))
            .gather(mapFallible2(this::throwsIfLessThanOne, true))
            .collect(toResultList());

        assertThat(results.successValues()).containsExactly(1);
        assertThat(results.failureCauses())
            .extracting(Cause.Double::exception)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new SomeException(), new OtherException());
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

    private static class SomeException extends Exception {}

    private static class OtherException extends Exception {}
}
