package com.wimp.app.specs.support;

import com.wimp.app.models.Order;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@ScenarioScope
public class OrderingContext {
    private Integer currentOrderNo;
    private final List<Order> placedOrders = new ArrayList<>();
    private Integer takenOrderNo;

    public Integer getCurrentOrderNo() {
        return currentOrderNo;
    }

    public void setCurrentOrderNo(Integer currentOrderNo) {
        this.currentOrderNo = currentOrderNo;
    }

    public int getCurrentOrderNoVerified() {
        return Optional.ofNullable(currentOrderNo)
            .orElseThrow(() -> new RuntimeException("No current order"));
    }

    public List<Order> getPlacedOrders() {
        return placedOrders;
    }

    public Integer getTakenOrderNo() {
        return takenOrderNo;
    }

    public void setTakenOrderNo(Integer takenOrderNo) {
        this.takenOrderNo = takenOrderNo;
    }
}
