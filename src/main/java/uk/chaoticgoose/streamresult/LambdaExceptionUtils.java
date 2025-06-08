package uk.chaoticgoose.streamresult;

import org.jspecify.annotations.NullMarked;

@NullMarked
final class LambdaExceptionUtils {

    @FunctionalInterface
    @NullMarked
    public interface FunctionWithException<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    @FunctionalInterface
    @NullMarked
    public interface SupplierWithException<T, E extends Exception> {
        T get() throws E;
    }
}
