package com.wimp.app.specs.support;

import io.cucumber.java.ParameterType;

import java.math.BigDecimal;

public class CustomParameterTypes {

    @ParameterType("\\$?(\\d+(?:\\.\\d{1,2})?)")
    public BigDecimal price(String value){
        return new BigDecimal(value);
    }
}
