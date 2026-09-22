package com.wimp.app.specs.support;

public class LambdaAction<T> extends TestAction<T> {

    /**
     * Helper interface for specifying lambda actions that throw exceptions.
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    private final ThrowingSupplier<T> action;

    public LambdaAction(String testActionName, Object input, ThrowingSupplier<T> action) {
        super(testActionName, input);
        this.action = action;
    }

    public LambdaAction(String testActionName, ThrowingSupplier<T> action) {
        this(testActionName, null, action);
    }

    @Override
    public T doExecute() throws Exception {
        return action.get();
    }

    /**
     * Lambda action for void-returning actions.
     */
    public static class Void extends LambdaAction<VoidReturn> {

        @FunctionalInterface
        public interface ThrowingRunnable {
            void run() throws Exception;
        }

        public Void(String testActionName, Object input, ThrowingRunnable action) {
            super(testActionName, input, () -> {
                action.run();
                return VoidReturn.INSTANCE;
            });
        }

        public Void(String testActionName, ThrowingRunnable action) {
            this(testActionName, null, action);
        }
    }
}
