package uk.chaoticgoose.streamresult;

import java.util.List;
import java.util.stream.Collector;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public final class ResultCollectors {
    private ResultCollectors() {}

    public static <T, C extends Cause> Collector<Result<T, C>, ?, ResultList<T, C>> toResultList() {
        return collectingAndThen(toList(), ResultList::new);
    }

    public static <T, C extends Cause> Collector<Result<T, C>, ?, Result<List<T>, C>> toSingleResult() {
        return collectingAndThen(toList(), (List<Result<T, C>> results) -> new ResultList<>(results).toSingleResult());
    }
}
