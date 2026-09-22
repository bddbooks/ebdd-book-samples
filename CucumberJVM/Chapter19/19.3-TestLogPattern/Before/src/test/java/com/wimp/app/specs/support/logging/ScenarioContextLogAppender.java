package com.wimp.app.specs.support.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import io.cucumber.java.Scenario;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ScenarioContextLogAppender extends AppenderBase<ILoggingEvent> {

    private static final DateTimeFormatter ISO_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").withZone(ZoneId.systemDefault());

    private static final ThreadLocal<Scenario> currentScenario = new ThreadLocal<>();
    private static final ThreadLocal<AppLogContext> currentAppLogContext = new ThreadLocal<>();

    public static void onScenarioStart(Scenario scenario, AppLogContext context) {
        currentScenario.set(scenario);
        currentAppLogContext.set(context);
    }

    public static void onScenarioEnd() {
        currentScenario.remove();
        currentAppLogContext.remove();
    }

    private static Scenario getScenario() {
        return currentScenario.get();
    }

    public static AppLogContext getAppLogContext() {
        return currentAppLogContext.get();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (event == null) return;

        Scenario scenario = getScenario();
        AppLogContext appLogContext = getAppLogContext();

        // If this thread isn't running a test scenario right now, ignore the log
        if (scenario == null || appLogContext == null) return;

        appendAppLogContext(event, appLogContext);
    }

    private void appendAppLogContext(ILoggingEvent event, AppLogContext appLogContext) {
        boolean isWimpTestLogEvent = event.getLoggerName().startsWith("com.wimp.app.specs");
        // In AppLogContext we only capture log events that come from the app,
        // not the ones come from the test code.
        if (isWimpTestLogEvent) {
            return;
        }

        String logMessage = event.getFormattedMessage();

        String logLine = String.format("%s  %-5s --- [%15.15s] %-40.40s : %s",
            ISO_FORMATTER.format(event.getInstant()), event.getLevel().toString(), event.getThreadName(), event.getLoggerName(), logMessage);

        org.slf4j.event.Level slf4jLevel = org.slf4j.event.Level.valueOf(event.getLevel().toString());
        appLogContext.addLogMessage(slf4jLevel, logLine);
    }
}
