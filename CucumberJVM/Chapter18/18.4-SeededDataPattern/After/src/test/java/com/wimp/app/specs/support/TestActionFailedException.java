package com.wimp.app.specs.support;

public class TestActionFailedException extends RuntimeException {
    public TestActionFailedException(String message) {
        super(message);
    }
}
