package uk.chaoticgoose.streamresult;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CauseTest {

    @Test
    void singleCauseThrowsException() {
        Exception1 exception = new Exception1();
        try {
            Cause.Single<Exception1> cause = Cause.Single.of(exception);
            cause.throwException();
            fail("Should have thrown an exception");
        } catch (Exception1 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    void doubleCauseThrowsException1() {
        Exception1 exception = new Exception1();
        try {
            Cause.Double<Exception1, Exception2> cause = Cause.Double.fromSingle(Cause.Single.of(exception));
            cause.throwExceptions();
            fail("Should have thrown an exception");
        } catch (Exception1 | Exception2 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    void doubleCauseThrowsException2() {
        Exception2 exception = new Exception2();
        try {
            Cause.Double<Exception1, Exception2> cause = Cause.Double.ofSecond(exception);
            cause.throwExceptions();
            fail("Should have thrown an exception");
        } catch (Exception1 | Exception2 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    void tripleCauseThrowsException1() {
        Exception1 exception = new Exception1();
        try {
            Cause.Triple<Exception1, Exception2, Exception3> cause = Cause.Triple.fromDouble(Cause.Double.fromSingle(Cause.Single.of(exception)));
            cause.throwExceptions();
            fail("Should have thrown an exception");
        } catch (Exception1 | Exception2 | Exception3 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    void tripleCauseThrowsException2() {
        Exception2 exception = new Exception2();
        try {
            Cause.Triple<Exception1, Exception2, Exception3> cause = Cause.Triple.fromDouble(Cause.Double.ofSecond(exception));
            cause.throwExceptions();
            fail("Should have thrown an exception");
        } catch (Exception1 | Exception2 | Exception3 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    void tripleCauseThrowsException3() {
        Exception3 exception = new Exception3();
        try {
            Cause.Triple<Exception1, Exception2, Exception3> cause = Cause.Triple.ofThird(exception);
            cause.throwExceptions();
            fail("Should have thrown an exception");
        } catch (Exception1 | Exception2 | Exception3 e) {
            assertThat(e).isEqualTo(exception);
        }
    }

    private static class Exception1 extends Exception {}

    private static class Exception2 extends Exception {}

    private static class Exception3 extends Exception {}
}