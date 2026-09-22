package com.wimp.app.specs.support;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class OrderingContext {
    private Integer currentOrderNo;

    public Integer getCurrentOrderNo() {
        return currentOrderNo;
    }

    public void setCurrentOrderNo(Integer currentOrderNo) {
        this.currentOrderNo = currentOrderNo;
    }
}
