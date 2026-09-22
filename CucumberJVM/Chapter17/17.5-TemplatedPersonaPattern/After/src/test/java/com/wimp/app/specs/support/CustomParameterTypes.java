package com.wimp.app.specs.support;

import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.time.LocalDate;
import java.util.Map;

public class CustomParameterTypes {

    @DataTableType
    public SalesFilterData salesFilterDataRow(Map<String, String> row) {
        return new SalesFilterData(
            row.get("exclude section"));
    }

    @ParameterType("\\d{4}-\\d{2}-\\d{2}")
    public LocalDate date(String value) {
        return LocalDate.parse(value);
    }
}
