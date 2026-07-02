package com.wimp.specs.support;

@FunctionalInterface
public interface TestAction<T> {
    T execute() throws Exception;
}
