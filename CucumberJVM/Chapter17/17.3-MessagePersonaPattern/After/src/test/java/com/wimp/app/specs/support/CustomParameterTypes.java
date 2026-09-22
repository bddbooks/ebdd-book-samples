package com.wimp.app.specs.support;

import com.wimp.app.models.*;
import com.wimp.app.services.MessageService;
import com.wimp.app.specs.drivers.CustomerDriver;
import io.cucumber.java.ParameterType;

import java.util.Arrays;

public class CustomParameterTypes {

    private final CustomerDriver customerDriver;
    private final MessageService messageService;

    public CustomParameterTypes(
        CustomerDriver customerDriver,
        MessageService messageService
    ) {
        this.customerDriver = customerDriver;
        this.messageService = messageService;
    }

    @ParameterType(value = "(10\"|12\"|14\")", name = "pizza-size")
    public PizzaSize convertPizzaSize(String pizzaSizeString) {
        return switch (pizzaSizeString) {
            case "10\"" -> PizzaSize.SMALL;
            case "12\"" -> PizzaSize.MEDIUM;
            case "14\"" -> PizzaSize.LARGE;
            default -> throw new IllegalArgumentException("Invalid size: " + pizzaSizeString);
        };
    }

    @ParameterType(value = "\\[([\\w\\-]+(?:,.+)?)\\]", name = "user-message")
    public String convertUserMessage(String messageNameSpecification) {
        String language = customerDriver.getInterfaceLanguage();
        String[] specParts = messageNameSpecification.split(",");
        String messageName = specParts[0];
        Object[] parameters = Arrays.copyOfRange(specParts, 1, specParts.length);
        return messageService.getMessage(language, messageName, parameters);
    }
}
