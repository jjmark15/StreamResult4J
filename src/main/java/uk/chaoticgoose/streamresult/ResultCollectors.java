package uk.chaoticgoose.streamresult;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.stream.Collector;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@NullMarked
public final class ResultCollectors {
    private ResultCollectors() {}

    public static <T, C extends Cause> Collector<StreamResult<T, C>, ?, ResultList<T, C>> toResultList() {
        return collectingAndThen(toList(), ResultList::new);
    }

    public static <T, C extends Cause> Collector<StreamResult<T, C>, ?, StreamResult<List<T>, C>> toSingleResult() {
        return collectingAndThen(toList(), (List<StreamResult<T, C>> results) -> new ResultList<>(results).toSingleResult());
    }
}
