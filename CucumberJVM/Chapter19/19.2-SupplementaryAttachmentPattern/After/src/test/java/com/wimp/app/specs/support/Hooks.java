package com.wimp.app.specs.support;

import com.wimp.app.specs.support.logging.AppLogContext;
import io.cucumber.java.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class Hooks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    private final TestFileSystem testFileSystem;
    private final AppLogContext appLogContext;

    public Hooks(TestFileSystem testFileSystem, AppLogContext appLogContext) {
        this.testFileSystem = testFileSystem;
        this.appLogContext = appLogContext;
    }

    // The after-scenario hook with a high order number ensures that it captures
    // the log before any other resources are disposed of.
    @After(order = 100)
    public void saveAppLogOnError(Scenario scenario) {
        if (scenario.getStatus() == Status.FAILED) {
            // A detailed discussion of the TestFileSystem class can be found in chapter 21: Test File System pattern
            var logFileName = testFileSystem.getScenarioSpecificFileName(".log");
            var outputPath = Paths.get(testFileSystem.getOutputFolder(), logFileName).toString();
            var logContent = appLogContext.saveToFile(outputPath);
            log.info("Saved app log to {}", outputPath);
            scenario.attach(logContent, "text/plain", logFileName);
        }
    }
}
