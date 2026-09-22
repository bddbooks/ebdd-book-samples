package com.wimp.app.specs.support;

import com.wimp.app.models.*;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.util.Map;

public class CustomParameterTypes {

    @ParameterType(value = "(10\"|12\"|14\")", name = "pizza-size")
    public PizzaSize convertPizzaSize(String pizzaSizeString) {
        return switch (pizzaSizeString) {
            case "10\"" -> PizzaSize.SMALL;
            case "12\"" -> PizzaSize.MEDIUM;
            case "14\"" -> PizzaSize.LARGE;
            default -> throw new IllegalArgumentException("Invalid size: " + pizzaSizeString);
        };
    }

    @DataTableType
    public ContactDetails contactDetailsDataRow(Map<String, String> row) {
        var contactDetails = DomainDefaults.contactDetailsDefaultInstance();
        if (row.containsKey("Name")) contactDetails.setName(row.get("Name"));
        if (row.containsKey("Email")) contactDetails.setEmail(row.get("Email"));
        if (row.containsKey("Phone")) contactDetails.setPhone(row.get("Phone"));
        if (row.containsKey("Country")) contactDetails.setCountry(row.get("Country"));
        if (row.containsKey("State")) contactDetails.setState(row.get("State"));
        return contactDetails;
    }
}
