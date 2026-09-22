package com.wimp.app.specs.support;

import static org.junit.jupiter.api.Assertions.*;

public class TestActionResult<TResult> {

    public static <TResult> TestActionResult<TResult> notExecuted() {
        return new TestActionResult<>(false, false, null, null);
    }

    public static <TResult> TestActionResult<TResult> createSucceeded(TResult result) {
        return new TestActionResult<>(true, true, null, result);
    }

    public static <TResult> TestActionResult<TResult> createSucceeded() {
        return createSucceeded(null);
    }

    public static <TResult> TestActionResult<TResult> createFailed(TestActionFailedException error) {
        return new TestActionResult<>(true, false, error, null);
    }

    private final boolean executed;
    private final boolean success;
    private final TestActionFailedException error;
    private final TResult result;

    public boolean wasExecuted() {
        return executed;
    }

    public boolean isSuccess() {
        return success;
    }

    public TestActionFailedException getError() {
        return error;
    }

    public TResult getResult() {
        return result;
    }

    private TestActionResult(boolean executed, boolean success, TestActionFailedException error, TResult result) {
        this.executed = executed;
        this.success = success;
        this.error = error;
        this.result = result;
    }

    public void assertExecuted() {
        assertTrue(wasExecuted(), "The action was not executed");
    }

    public void assertFailed() {
        assertExecuted();
        assertFalse(isSuccess(), "The result (%s) expected to be failure".formatted(this));
        assertNotNull(getError(), "The result (%s) expected to contain an error".formatted(this));
    }

    public void assertFailedWithErrorMessageContains(String expectedErrorMessage) {
        assertFailed();
        assertTrue(getError().getMessage().contains(expectedErrorMessage), "The error `%s` is expected to contain message `%s`".formatted(getError().getMessage(), expectedErrorMessage));
    }

    public void assertSucceeded() {
        assertExecuted();
        assertTrue(isSuccess(), "The result (%s) expected to succeed".formatted(this));
    }
}
