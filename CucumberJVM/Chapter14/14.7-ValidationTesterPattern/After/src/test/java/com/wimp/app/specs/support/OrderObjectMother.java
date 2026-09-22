package com.wimp.app.specs.support;

import com.wimp.app.models.*;

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

    public OrderObjectMother withAdditionalItem(String name, PizzaSize size, PizzaStyle style) {
        PizzaItem pizzaItem = DomainDefaults.pizzaItemDefaultInstance(name, size, style);
        order.addItem(pizzaItem);
        return this;
    }

    public OrderObjectMother withItem(String name, PizzaSize size, PizzaStyle style) {
        order.clearItems(); // remove existing items
        return withAdditionalItem(name, size, style);
    }

    public OrderObjectMother withItem(String name) {
        return withItem(name, null, null);
    }

    public OrderObjectMother withItems(int quantity, String name, PizzaSize size, PizzaStyle style) {
        order.clearItems(); // remove existing items
        for (int i = 0; i < quantity; i++) {
            withAdditionalItem(name, size, style);
        }
        return this;
    }

    public OrderObjectMother withItems(int quantity, PizzaSize size) {
        return withItems(quantity, null, size, null);
    }

    public OrderObjectMother withCustomerCollection() {
        order.setDeliveryMethod(DeliveryMethod.CUSTOMER_COLLECTION);
        return this;
    }
}
