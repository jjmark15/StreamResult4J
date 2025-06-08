package uk.chaoticgoose.streamresult;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CauseTest {

    @Test
    void singleCauseThrowsException() {
        var exception = new Exception1();
        try {
            Cause.Single<@NonNull Exception1> cause = Cause.Single.of(exception);
            cause.throwException();
            fail("Should have thrown an exception");
        } catch (Exception1 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    void singleCauseReturnsException() {
        var exception = new Exception1();
        assertThat(Cause.Single.of(exception).exception()).isEqualTo(exception);
    }

    @Test
    void doubleCauseThrowsException1() {
        var exception = new Exception1();
        try {
            Cause.Double<@NonNull Exception1, @NonNull Exception2> cause = Cause.Double.fromSingle(Cause.Single.of(exception));
            cause.throwExceptions();
            fail("Should have thrown an exception");
        } catch (Exception1 | Exception2 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    void doubleCauseReturnsException1() {
        var exception = new Exception1();
        assertThat(Cause.Double.fromSingle(Cause.Single.of(exception)).exception()).isEqualTo(exception);
    }

    @Test
    void doubleCauseThrowsException2() {
        var exception = new Exception2();
        try {
            Cause.Double<@NonNull Exception1, @NonNull Exception2> cause = Cause.Double.ofSecond(exception);
            cause.throwExceptions();
            fail("Should have thrown an exception");
        } catch (Exception1 | Exception2 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    void doubleCauseReturnsException2() {
        var exception = new Exception2();
        assertThat(Cause.Double.ofSecond(exception).exception()).isEqualTo(exception);
    }

    @Test
    void tripleCauseThrowsException1() {
        var exception = new Exception1();
        try {
            Cause.Triple<@NonNull Exception1, @NonNull Exception2, @NonNull Exception3> cause = Cause.Triple.fromDouble(Cause.Double.fromSingle(Cause.Single.of(exception)));
            cause.throwExceptions();
            fail("Should have thrown an exception");
        } catch (Exception1 | Exception2 | Exception3 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    void tripleCauseReturnsException1() {
        var exception = new Exception1();
        assertThat(Cause.Triple.fromDouble(Cause.Double.fromSingle(Cause.Single.of(exception))).exception()).isEqualTo(exception);
    }

    @Test
    void tripleCauseThrowsException2() {
        var exception = new Exception2();
        try {
            Cause.Triple<@NonNull Exception1, @NonNull Exception2, @NonNull Exception3> cause = Cause.Triple.fromDouble(Cause.Double.ofSecond(exception));
            cause.throwExceptions();
            fail("Should have thrown an exception");
        } catch (Exception1 | Exception2 | Exception3 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    void tripleCauseReturnsException2() {
        var exception = new Exception2();
        assertThat(Cause.Triple.fromDouble(Cause.Double.ofSecond(exception)).exception()).isEqualTo(exception);
    }

    @Test
    void tripleCauseThrowsException3() {
        Exception3 exception = new Exception3();
        try {
            Cause.Triple<@NonNull Exception1, @NonNull Exception2, @NonNull Exception3> cause = Cause.Triple.ofThird(exception);
            cause.throwExceptions();
            fail("Should have thrown an exception");
        } catch (Exception1 | Exception2 | Exception3 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    void tripleCauseReturnsException3() {
        var exception = new Exception3();
        assertThat(Cause.Triple.ofThird(exception).exception()).isEqualTo(exception);
    }

    private static class Exception1 extends Exception {}

    private static class Exception2 extends Exception {}

    private static class Exception3 extends Exception {}
}