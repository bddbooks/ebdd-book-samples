package com.wimp.app.specs.support;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@ScenarioScope
public class OrderingContext {
    private Integer currentOrderNo;

    public Integer getCurrentOrderNo() {
        return currentOrderNo;
    }

    public int getCurrentOrderNoVerified() {
        return Optional.ofNullable(currentOrderNo)
            .orElseThrow(() -> new RuntimeException("No current order"));
    }

    public void setCurrentOrderNo(Integer currentOrderNo) {
        this.currentOrderNo = currentOrderNo;
    }
}
