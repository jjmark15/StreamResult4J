package uk.chaoticgoose.streamresult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.chaoticgoose.streamresult.Result.Failure;
import uk.chaoticgoose.streamresult.Result.Success;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResultTest {
    private static final String VALUE = "value";
    private static final SomeException EXCEPTION = new SomeException();
    private static final Cause SINGLE_CAUSE = Cause.Single.of(EXCEPTION);
    @SuppressWarnings("unchecked")
    private static final Function<Exception, Cause> mockCauseFunction = mock(Function.class);
    private static final Success<String, Cause> SUCCESS = new Success<>(VALUE);
    private static final Failure<String, Cause> FAILURE = new Failure<>(SINGLE_CAUSE);

    @BeforeEach
    void setUp() {
        when(mockCauseFunction.apply(EXCEPTION)).thenReturn(SINGLE_CAUSE);
    }

    @Test
    void successIsNotAFailure() {
        assertThat(SUCCESS.isFailure()).isFalse();
    }

    @Test
    void failureIsAFailure() {
        assertThat(new Failure<>(new Cause.Single<>(new Exception())).isFailure()).isTrue();
    }

    @Test
    void success_returnsSuccessWhenSuccess() {
        assertThat(SUCCESS.success()).hasValue(SUCCESS);
    }

    @Test
    void success_returnsEmptyWhenNotSuccess() {
        assertThat(FAILURE.success()).isEmpty();
    }

    @Test
    void failure_returnsFailureWhenFailure() {
        assertThat(FAILURE.failure()).hasValue(FAILURE);
    }

    @Test
    void failure_returnsEmptyWhenNotFailure() {
        assertThat(SUCCESS.failure()).isEmpty();
    }

    @Test
    void valueOrThrow_throwsIllegalStateExceptionIfResultIsFailure() {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(FAILURE::valueOrThrow);
    }

    @Test
    void valueOrThrow_returnsValueIfResultIsSuccess() {
        assertThat(SUCCESS.valueOrThrow()).isEqualTo(VALUE);
    }

    @Test
    void causeOrThrow_throwsIllegalStateExceptionIfResultIsSuccess() {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(SUCCESS::causeOrThrow);
    }

    @Test
    void causeOrThrow_returnsCauseIfResultIsFailure() {
        assertThat(FAILURE.causeOrThrow()).isEqualTo(SINGLE_CAUSE);
    }

    @Test
    void mapsSuccessValues() {
        assertThat(SUCCESS.mapSuccess(String::length)).isEqualTo(new Success<>(VALUE.length()));
    }

    @Test
    void wrapsValueInResult() {
        assertThat(Result.catching(() -> methodThatCanThrow(VALUE), mockCauseFunction)).isEqualTo(SUCCESS);
    }

    @Test
    void wrapsThrownExceptionInResult() {
        assertThat(Result.catching(() -> methodThatThrows(VALUE), mockCauseFunction)).isEqualTo(FAILURE);
    }

    private static String methodThatCanThrow(String string) throws SomeException {
        return string;
    }

    private static String methodThatThrows(String ignore) throws SomeException {
        throw EXCEPTION;
    }

    private static class SomeException extends Exception {}
}