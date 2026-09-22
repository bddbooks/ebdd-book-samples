package com.wimp.app.specs.support;

import io.cucumber.java.ParameterType;

import java.time.LocalDate;

public class CustomParameterTypes {

    @ParameterType("\\d{4}-\\d{2}-\\d{2}")
    public LocalDate date(String value) {
        return LocalDate.parse(value);
    }
}
