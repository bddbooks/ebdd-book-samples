package com.wimp.app.specs.support;

import com.wimp.app.models.PizzaItem;
import com.wimp.app.models.PizzaSize;
import com.wimp.app.models.PizzaStyle;
import com.wimp.app.restapi.PlaceOrderRequest;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrderRequestObjectMother {
    private final List<PizzaItem> items = new ArrayList<>();

    public PlaceOrderRequestObjectMother() {
        // add default item
        items.add(DomainDefaults.pizzaItemDefaultInstance());
    }

    public PlaceOrderRequest build() {
        return new PlaceOrderRequest(
            items.toArray(new PizzaItem[0]),
            DomainDefaults.CUSTOMER_ADDRESS,
            DomainDefaults.CUSTOMER_EMAIL);
    }

    public PlaceOrderRequestObjectMother withAdditionalItem(String name, PizzaSize size, PizzaStyle style) {
        PizzaItem pizzaItem = DomainDefaults.pizzaItemDefaultInstance(name, size, style);
        items.add(pizzaItem);
        return this;
    }

    public PlaceOrderRequestObjectMother withAdditionalItem() {
        return withAdditionalItem(null, null, null);
    }

    public PlaceOrderRequestObjectMother withItem(String name, PizzaSize size, PizzaStyle style) {
        items.clear(); // remove existing items
        return withAdditionalItem(name, size, style);
    }

    public PlaceOrderRequestObjectMother withItem(String name) {
        return withItem(name, null, null);
    }

    public PlaceOrderRequestObjectMother withItem() {
        return withItem(null, null, null);
    }

    public PlaceOrderRequestObjectMother withItems(int quantity, String name, PizzaSize size, PizzaStyle style) {
        items.clear(); // remove existing items
        for (int i = 0; i < quantity; i++) {
            withAdditionalItem(name, size, style);
        }
        return this;
    }

    public PlaceOrderRequestObjectMother withItems(int quantity) {
        return withItems(quantity, null, null, null);
    }

    public PlaceOrderRequestObjectMother withItems(int quantity, PizzaSize size) {
        return withItems(quantity, null, size, null);
    }
}
