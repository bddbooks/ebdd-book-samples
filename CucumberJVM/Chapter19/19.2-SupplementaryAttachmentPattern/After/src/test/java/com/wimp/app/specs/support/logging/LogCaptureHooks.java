package com.wimp.app.specs.support.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.LoggerFactory;

public class LogCaptureHooks {

    private final AppLogContext appLogContext;

    public LogCaptureHooks(AppLogContext appLogContext) {
        this.appLogContext = appLogContext;
    }

    private static boolean isRegistered = false;

    @Before(order = -100) // The log appender must be initialized in the before-hook of the first scenario because with @BeforeAll, the registration would be too early
    public void registerScenarioContextLogAppender(){
        if (isRegistered)
            return;

        isRegistered = true;

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);

        ScenarioContextLogAppender scenarioContextLogAppender = new ScenarioContextLogAppender();
        scenarioContextLogAppender.setContext(context);
        scenarioContextLogAppender.start();
        rootLogger.addAppender(scenarioContextLogAppender);
    }

    @Before(order = -1) // Runs early to capture initial scenario execution logs
    public void startLogCapture(Scenario scenario) {
        ScenarioContextLogAppender.onScenarioStart(scenario, appLogContext);
    }

    @After(order = -1) // Runs late to clean up references and check final health metrics
    public void stopLogCapture() {
        ScenarioContextLogAppender.onScenarioEnd();
    }
}
