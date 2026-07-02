package com.wimp.specs.support;

import com.wimp.app.services.TimeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
