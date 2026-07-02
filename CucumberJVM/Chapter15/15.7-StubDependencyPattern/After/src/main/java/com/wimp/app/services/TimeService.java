package com.wimp.app.services;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public interface TimeService {
    LocalDateTime getCurrentTime();

    void subscribeToTimeChange(Predicate<LocalDateTime> onTimeChanged);
}
