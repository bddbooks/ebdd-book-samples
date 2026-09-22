package com.wimp.app.specs.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    private final TestFileSystem testFileSystem;

    public Hooks(TestFileSystem testFileSystem) {
        this.testFileSystem = testFileSystem;
    }
}
