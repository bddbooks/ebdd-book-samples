package com.wimp.app.specs.support;

import com.wimp.app.specs.support.logging.AppLogContext;
import io.cucumber.java.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class Hooks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    private final AppLogContext appLogContext;

    public Hooks(AppLogContext appLogContext) {
        this.appLogContext = appLogContext;
    }

    // The after-scenario hook with a high order number ensures that it captures
    // the log before any other resources are disposed of.
    @After(order = 100)
    public void saveAppLogOnError(Scenario scenario) throws IOException {
        if (scenario.getStatus() == Status.FAILED) {
            // Calculating a unique log file name
            var logFileName = "app-log-%s.log".formatted(UUID.randomUUID().toString());
            // Calculating the expected output folder.
            var outputFolder = Paths.get(System.getProperty("user.dir"), "target", "wimp-output");
            if (!Files.exists(outputFolder)) {
                Files.createDirectories(outputFolder);
            }
            var outputPath = Paths.get(outputFolder.toString(), logFileName).toString();
            var logContent = appLogContext.saveToFile(outputPath);
            log.info("Saved app log to {}", outputPath);
            scenario.attach(logContent, "text/plain", logFileName);
        }
    }
}
