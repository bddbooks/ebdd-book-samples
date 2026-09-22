package com.wimp.app.specs.support.logging;

import com.wimp.app.specs.support.CorroborationCheckException;
import io.cucumber.spring.ScenarioScope;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Scenario context to collect log entries from the hosted WIMP application. The
 * log entries can be saved to an attachment or can be used for diagnosis.
 */
@Component
@ScenarioScope
public class AppLogContext {

    private final ConcurrentLinkedQueue<String> logMessages = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> healthIssues = new ConcurrentLinkedQueue<>();

    /**
     * Returns an unmodifiable view of the captured log messages.
     */
    public Collection<String> getLogMessages() {
        return Collections.unmodifiableCollection(logMessages);
    }

    public void addLogMessage(Level logLevel, String logMessage) {
        logMessages.add(logMessage);
        if (logLevel.toInt() >= Level.WARN.toInt()) {
            healthIssues.add(logMessage);
        }
    }

    public void checkAppHealth() {
        if (!healthIssues.isEmpty()) {
            String issueText = String.join(System.lineSeparator(), healthIssues);
            throw new CorroborationCheckException(
                "The application log contains warnings or errors:" + System.lineSeparator() + issueText
            );
        }
    }

    public String saveToFile(String outputPath) {
        String logContent = String.join(System.lineSeparator(), logMessages);
        try {
            Files.writeString(Paths.get(outputPath), logContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save application logs to " + outputPath, e);
        }
        return logContent;
    }
}
