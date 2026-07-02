package com.wimp.app.services;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Service
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
