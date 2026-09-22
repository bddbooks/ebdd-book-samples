package com.wimp.app.specs.support;

import com.wimp.app.services.TimeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This is a stub implementation of the TimeService interface for testing
 * purposes. An alternative approach would be to use the "RealTimeService"
 * implementation and mock the Clock dependency, but this approach also
 * simplifies the time-based subscription logic for testing.
 * <p>
 * For an idiomatic Spring Boot solution for time-related functionality, check
 * out the comments in the "TimeService" interface in the main source code.
 * <p>
 * The configuration that exposes the StubTimeService and uses it as TimeService
 * can be found at the CucumberSpringConfiguration.StubDependencyConfiguration
 * class. Check the additional stubbing notes there.
 *
 * @see com.wimp.app.CucumberSpringConfiguration.StubDependencyConfiguration
 */
public class StubTimeService implements TimeService {
    private LocalDateTime now = LocalDateTime.now();
    private final List<Predicate<LocalDateTime>> timeChangeSubscribers = new ArrayList<>();

    @Override
    public LocalDateTime getCurrentTime() {
        return now;
    }

    @Override
    public void subscribeToTimeChange(Predicate<LocalDateTime> onTimeChanged) {
        timeChangeSubscribers.add(onTimeChanged);
    }

    public void setCurrentTime(LocalDateTime currentDateTime) {
        now = currentDateTime;
        triggerTimeChange();
    }

    public void triggerTimeChange() {
        LocalDateTime currentDateTime = getCurrentTime();
        for (Predicate<LocalDateTime> subscriber : List.copyOf(timeChangeSubscribers)) {
            if (subscriber.test(currentDateTime)) {
                timeChangeSubscribers.remove(subscriber);
            }
        }
    }
}
