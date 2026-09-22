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

import java.time.LocalDateTime;
import java.util.function.Predicate;

/**
 * This interface encapsulates the time-related functionality of the
 * application: getting the current time and subscribing to time change events.
 * <p>
 * In an idiomatic Java Spring Boot application, this functionality would
 * typically be provided via the Clock abstraction and Spring Boot’s event
 * system.
 * <p>
 * Our goal with this interface was to have a simple and testable abstraction,
 * not to provide an idiomatic Spring Boot time-related functionality
 * implementation.
 * <p>
 * Check out the "RealTimeService" class in the main source code for a real
 * implementation and "StubTimeService" class in the test source code for a stub
 * implementation of this interface.
 *
 * @see RealTimeService
 */
public interface TimeService {
    LocalDateTime getCurrentTime();

    void subscribeToTimeChange(Predicate<LocalDateTime> onTimeChanged);
}
