package uk.chaoticgoose.streamresult;

import java.util.List;
import java.util.Optional;

public final class ResultList<T, C extends Cause> {
    private final List<Result<T, C>> results;

    ResultList(List<Result<T, C>> results) {
        this.results = results;
    }

    public Optional<Result.Failure<T, C>> anyFailure() {
        return anyFailureResult();
    }

    public Result<List<T>, C> toSingleResult() {
        return anyFailureResult().map(ResultList::adapt).orElseGet(this::successList);
    }

    private Optional<Result.Failure<T, C>> anyFailureResult() {
        return results.stream().filter(Result::isFailure).map(Result::failureOrThrow).findAny();
    }

    private Result.Success<List<T>, C> successList() {
        return new Result.Success<>(results.stream().map(Result::valueOrThrow).toList());
    }

    private static <T, C extends Cause> Result<List<T>, C> adapt(Result.Failure<T, C> f) {
        return new Result.Failure<>(f.cause());
    }
}
