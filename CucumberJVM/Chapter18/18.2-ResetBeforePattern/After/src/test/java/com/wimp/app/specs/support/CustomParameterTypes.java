package com.wimp.app.specs.support;

import com.wimp.app.models.*;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.math.BigDecimal;
import java.util.Map;

public class CustomParameterTypes {

    @DataTableType
    public MenuItemData menuItemDataRow(Map<String, String> row) {
        return new MenuItemData(
            row.get("name"),
            row.containsKey("price") ? new BigDecimal(row.get("price")) : null,
            row.containsKey("calories") ? Integer.valueOf(row.get("calories")) : null,
            row.containsKey("vegetarian") && Boolean.parseBoolean(row.get("vegetarian")));
    }

    @ParameterType("\\$?(\\d+(?:\\.\\d{1,2})?)")
    public BigDecimal price(String value){
        return new BigDecimal(value);
    }
}
