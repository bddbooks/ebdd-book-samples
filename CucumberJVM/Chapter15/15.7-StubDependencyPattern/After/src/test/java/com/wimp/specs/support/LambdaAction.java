package com.wimp.specs.support;

public class LambdaAction<T> implements TestAction<T> {
    @FunctionalInterface
    public interface ThrowingSupplier<TValue> {
        TValue get() throws Exception;
    }

    private final ThrowingSupplier<T> action;

    public LambdaAction(ThrowingSupplier<T> action) {
        this.action = action;
    }

    @Override
    public T execute() throws Exception {
        return action.get();
    }
}
