package com.wimp.app.specs.support;

import java.math.BigDecimal;

public record OfferedItemData(String name, BigDecimal price, BigDecimal originalPrice) {
}
