package uk.chaoticgoose.streamresult;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public sealed interface Cause {
    Exception exception();

    @NullMarked
    record Single<E extends Exception>(E e) implements Cause {
        public static <E extends Exception> Single<E> of(E exception) {
            return new Single<>(exception);
        }

        public void throwException() throws E {
            throw e;
        }

        @Override
        public Exception exception() {
            return e;
        }
    }

    @NullMarked
    record Double<E1 extends Exception, E2 extends Exception>(@Nullable E1 e1, @Nullable E2 e2) implements Cause {
        public static <E1 extends Exception, E2 extends Exception> Double<E1, E2> fromSingle(Single<E1> single) {
            return new Double<>(single.e(), null);
        }

        public static <E1 extends Exception, E2 extends Exception> Double<E1, E2> ofSecond(E2 exception) {
            return new Double<>(null, exception);
        }

        public void throwExceptions() throws E1, E2 {
            if (e1 != null) {
                throw e1;
            }
            throw requireNonNull(e2);
        }

        @Override
        public Exception exception() {
            if (e1 != null) return e1;
            return requireNonNull(e2);
        }
    }

    @NullMarked
    record Triple<E1 extends Exception, E2 extends Exception, E3 extends Exception>(@Nullable E1 e1, @Nullable E2 e2,@Nullable E3 e3) implements Cause {
        public static <E1 extends Exception, E2 extends Exception, E3 extends Exception> Triple<E1, E2, E3> fromDouble(Double<E1, E2> previous) {
            return new Triple<>(previous.e1(), previous.e2(), null);
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
            throw requireNonNull(e3);
        }

        @Override
        public Exception exception() {
            if (e1 != null) return e1;
            if (e2 != null) return e2;
            return requireNonNull(e3);
        }
    }
}
