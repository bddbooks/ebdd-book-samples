package com.wimp.app.specs.support;

import java.math.BigDecimal;

public record MenuItemData(String name, BigDecimal price, Integer calories, String ingredients) {
}
