package com.wimp.app.specs.support;

import com.wimp.app.models.Order;
import com.wimp.app.specs.drivers.OrderingApiDriver;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@ScenarioScope
public class OrderingContext {
    private final OrderingApiDriver orderingApiDriver;

    private Integer currentOrderNo;
    private final List<Order> placedOrders = new ArrayList<>();
    private final Map<String, Integer> namedOrders = new HashMap<>();
    private Integer takenOrderNo;

    public OrderingContext(OrderingApiDriver orderingApiDriver) {
        this.orderingApiDriver = orderingApiDriver;
    }

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

    public List<Order> getPlacedOrders() {
        return placedOrders;
    }

    public Map<String, Integer> getNamedOrders() {
        return namedOrders;
    }

    public Integer getTakenOrderNo() {
        return takenOrderNo;
    }

    public void setTakenOrderNo(Integer takenOrderNo) {
        this.takenOrderNo = takenOrderNo;
    }

    public void ensureOrderPlaced() throws Exception {
        if (currentOrderNo == null) {
            var orderRequest = new PlaceOrderRequestObjectMother().build();
            var placedOrder = orderingApiDriver.placeOrder(orderRequest).execute();
            currentOrderNo = placedOrder.getOrderNo();
        }
    }
}
