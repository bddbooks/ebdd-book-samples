/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

package com.wimp.app.services;

import jakarta.annotation.PreDestroy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * This class implements the time-related functionality of the application:
 * getting the current time and subscribing to time change events.
 * <p>
 * In an idiomatic Java Spring Boot application, this functionality would
 * typically be provided via the Clock abstraction and Spring Boot’s event
 * system.
 * <p>
 * Our goal with this class was to have a simple and testable abstraction, not
 * to provide an idiomatic Spring Boot time-related functionality
 * implementation.
 * <p>
 * Check out the "StubTimeService" class in the test source code for a stub
 * implementation of this interface.
 *
 * @see com.wimp.app.WimpAutoConfiguration for bean configuration.
 */
public class RealTimeService implements TimeService {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final List<Predicate<LocalDateTime>> timeChangeSubscribers = new CopyOnWriteArrayList<>();

    public RealTimeService() {
        scheduler.scheduleAtFixedRate(this::triggerTimeChange, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    @Override
    public void subscribeToTimeChange(Predicate<LocalDateTime> onTimeChanged) {
        timeChangeSubscribers.add(onTimeChanged);
    }

    private void triggerTimeChange() {
        LocalDateTime currentDateTime = getCurrentTime();
        for (Predicate<LocalDateTime> subscriber : List.copyOf(timeChangeSubscribers)) {
            if (subscriber.test(currentDateTime)) {
                timeChangeSubscribers.remove(subscriber);
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
