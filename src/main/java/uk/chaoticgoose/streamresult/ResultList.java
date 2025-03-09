package uk.chaoticgoose.streamresult;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

public final class ResultList<T, C extends Cause> {
    private final List<Result<T, C>> results;

    ResultList(List<Result<T, C>> results) {
        this.results = results;
    }

    public Optional<Result.Failure<T, C>> anyFailure() {
        return anyFailureResult();
    }

    public Result<List<T>, C> toSingleResult() {
        return anyFailureResult().map(ResultList::adapt).orElseGet(this::successes);
    }

    private Optional<Result.Failure<T, C>> anyFailureResult() {
        return results.stream().filter(Result::isFailure).map(result -> switch (result) {
            case Result.Success<?, ?> _ -> throw new IllegalStateException("Result is not a Failure");
            case Result.Failure<T, C> failure -> failure;
        }).findAny();
    }

    private Result.Success<List<T>, C> successes() {
        return new Result.Success<>(results.stream().filter(not(Result::isFailure)).map(Result::valueOrThrow).toList());
    }

    public List<T> successValues() {
        return successes().value();
    }

    public List<C> failureCauses() {
        return results.stream().filter(Result::isFailure).map(Result::causeOrThrow).toList();
    }

    private static <T, C extends Cause> Result<List<T>, C> adapt(Result.Failure<T, C> f) {
        return new Result.Failure<>(f.cause());
    }
}
