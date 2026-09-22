package com.wimp.app.specs.support;

import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.math.BigDecimal;
import java.util.Map;

public class CustomParameterTypes {

    @DataTableType
    public MenuItemData menuItemDataRow(Map<String, String> row) {
        return new MenuItemData(
            row.get("name"),
            row.containsKey("price") ? new BigDecimal(row.get("price")) : DomainDefaults.PIZZA_PRICE,
            row.containsKey("calories") ? Integer.parseInt(row.get("calories")) : DomainDefaults.PIZZA_CALORIES,
            row.getOrDefault("ingredients", DomainDefaults.PIZZA_INGREDIENTS),
            row.containsKey("vegetarian") ? Boolean.parseBoolean(row.get("vegetarian")) : DomainDefaults.PIZZA_VEGETARIAN);
    }

    @ParameterType("\\$?(\\d+(?:\\.\\d{1,2})?)")
    public BigDecimal price(String value){
        return new BigDecimal(value);
    }
}
