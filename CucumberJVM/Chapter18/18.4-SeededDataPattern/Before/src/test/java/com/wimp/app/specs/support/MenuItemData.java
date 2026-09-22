package com.wimp.app.specs.support;

import java.math.BigDecimal;

public record MenuItemData(String name, BigDecimal price, Integer calories, boolean vegetarian) {
    public MenuItemData(String name) {
        this(name, null, null, false);
    }
}
