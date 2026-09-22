package com.wimp.app.specs.support;

import com.wimp.app.models.Order;
import com.wimp.app.models.PizzaItem;
import com.wimp.app.models.PizzaStyle;

public class OrderObjectMother {
    private final Order order;

    public OrderObjectMother() {
        order = new Order();
        // add default item
        order.addItem(DomainDefaults.pizzaItemDefaultInstance());
        order.setDeliveryAddress(DomainDefaults.CUSTOMER_ADDRESS);
    }

    public Order build() {
        return order;
    }

    public OrderObjectMother withAdditionalItem(String name, String size, PizzaStyle style) {
        PizzaItem pizzaItem = DomainDefaults.pizzaItemDefaultInstance(name, size, style);
        order.addItem(pizzaItem);
        return this;
    }

    public OrderObjectMother withItem(String name, String size, PizzaStyle style) {
        order.clearItems(); // remove existing items
        return withAdditionalItem(name, size, style);
    }

    public OrderObjectMother withItem(String name) {
        return withItem(name, null, null);
    }

    public OrderObjectMother withItems(int quantity, String name, String size, PizzaStyle style) {
        order.clearItems(); // remove existing items
        for (int i = 0; i < quantity; i++) {
            withAdditionalItem(name, size, style);
        }
        return this;
    }

    public OrderObjectMother withItems(int quantity, String size) {
        return withItems(quantity, null, size, null);
    }
}
