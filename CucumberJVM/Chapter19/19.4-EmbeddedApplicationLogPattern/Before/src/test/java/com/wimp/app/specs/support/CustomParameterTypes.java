package com.wimp.app.specs.support;

import com.wimp.app.models.*;
import io.cucumber.java.ParameterType;

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
}
