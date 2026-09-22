package com.wimp.app.specs.drivers;

import org.springframework.stereotype.Component;

@Component
public class CustomerDriver {

    public String getInterfaceLanguage() {
        return "en-US"; // fixed value is returned for the sake of demonstration
    }
}
