package uk.chaoticgoose.streamresult;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;

@NullMarked
public final class ResultList<T, C extends Cause> {
    private final List<StreamResult<T, C>> results;

    ResultList(List<StreamResult<T, C>> results) {
        this.results = results;
    }

    public Optional<StreamResult.Failure<T, C>> anyFailure() {
        return anyFailureResult();
    }

    public StreamResult<List<T>, C> toSingleResult() {
        return anyFailureResult().map(ResultList::adapt).orElseGet(this::successes);
    }

    private Optional<StreamResult.Failure<T, C>> anyFailureResult() {
        return results.stream().filter(StreamResult::isFailure).map(result -> switch (result) {
            case StreamResult.Success<?, ?> _ -> throw new IllegalStateException("Result is not a Failure");
            case StreamResult.Failure<T, C> failure -> failure;
        }).findAny();
    }

    private StreamResult.Success<List<T>, C> successes() {
        return new StreamResult.Success<>(results.stream().filter(StreamResult::isSuccess).map(StreamResult::valueOrThrow).toList());
    }

    public List<T> successValues() {
        return successes().value();
    }

    public List<C> failureCauses() {
        return results.stream().filter(StreamResult::isFailure).map(StreamResult::causeOrThrow).toList();
    }

    private static <T, C extends Cause> StreamResult<List<T>, C> adapt(StreamResult.Failure<T, C> f) {
        return new StreamResult.Failure<>(f.cause());
    }
}
