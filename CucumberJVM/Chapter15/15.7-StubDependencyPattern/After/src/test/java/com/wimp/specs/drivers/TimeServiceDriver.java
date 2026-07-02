package com.wimp.specs.drivers;

import com.wimp.specs.support.StubTimeService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class TimeServiceDriver {
    private final StubTimeService stubTimeService;

    public TimeServiceDriver(StubTimeService stubTimeService) {
        this.stubTimeService = stubTimeService;
    }

    public void setCurrentTime(LocalTime time) {
        LocalDateTime dateTime = getTodayTime(time);
        stubTimeService.setCurrentTime(dateTime);
    }

    public LocalDateTime getTodayTime(LocalTime time) {
        return stubTimeService.getCurrentTime().with(time);
    }
}
