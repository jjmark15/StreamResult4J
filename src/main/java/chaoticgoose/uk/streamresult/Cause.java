package chaoticgoose.uk.streamresult;

public sealed interface Cause {
    record Single<E extends Exception>(E cause) implements Cause {
        public static <E extends Exception> Single<E> of(E exception) {
            return new Single<>(exception);
        }

        public void throwException() throws E {
            throw cause;
        }
    }

    record Double<E1 extends Exception, E2 extends Exception>(E1 cause1, E2 cause2) implements Cause {
        public static <E1 extends Exception, E2 extends Exception> Double<E1, E2> fromSingle(Single<E1> single) {
            return new Double<>(single.cause(), null);
        }

        public static <E1 extends Exception, E2 extends Exception> Double<E1, E2> ofSecond(E2 exception) {
            return new Double<>(null, exception);
        }

        public void throwExceptions() throws E1, E2 {
            if (cause1 != null) {
                throw cause1;
            }
            throw cause2;
        }
    }

    record Triple<E1 extends Exception, E2 extends Exception, E3 extends Exception>(E1 e1, E2 e2, E3 e3) implements Cause {
        public static <E1 extends Exception, E2 extends Exception, E3 extends Exception> Triple<E1, E2, E3> fromDouble(Double<E1, E2> previous) {
            return new Triple<>(previous.cause1(), previous.cause2(), null);
        }

        public static <E1 extends Exception, E2 extends Exception, E3 extends Exception> Triple<E1, E2, E3> ofThird(E3 exception) {
            return new Triple<>(null, null, exception);
        }

        public void throwExceptions() throws E1, E2, E3 {
            if (e1 != null) {
                throw e1;
            }
            if (e2 != null) {
                throw e2;
            }
            throw e3;
        }
    }
}
