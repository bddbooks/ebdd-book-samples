package com.wimp.app.specs.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestAction<TResult> {

    protected static final Logger log = LoggerFactory.getLogger(TestAction.class);

    private final String testActionName;
    private final Object input;

    public String getTestActionName() {
        return testActionName;
    }

    public Object getInput() {
        return input;
    }

    public TestAction(String testActionName, Object input) {
        this.testActionName = testActionName;
        this.input = input;
    }

    public TestAction(String testActionName) {
        this(testActionName, null);
    }

    abstract TResult doExecute() throws Exception;

    public final TResult execute() throws Exception {
        log.info("Executing {} with {}...", getTestActionName(), getInput());
        long startTime = System.nanoTime();
        try {
            TResult result = doExecute();
            log.info("{} executed successfully in {} ms with {}.", getTestActionName(), (System.nanoTime() - startTime) / 1_000_000, result);
            return result;
        } catch (Exception ex) {
            log.error("{} failed: {}", getTestActionName(), ex.getMessage());
            if (ex instanceof TestActionFailedException)
                throw ex;
            throw new TestActionFailedException("%s failed: %s".formatted(getTestActionName(), ex.getMessage()));
        }
    }
}
