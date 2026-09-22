package com.wimp.app.specs.support;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ScenarioScope
public class AuthenticationContext {
    private String authenticatedCustomerName;

    public String getAuthenticatedCustomerName() {
        return authenticatedCustomerName;
    }

    public String getAuthenticatedCustomerNameVerified() {
        return Optional.ofNullable(authenticatedCustomerName)
            .orElseThrow(() -> new RuntimeException("No authenticated customer"));
    }

    public void setAuthenticatedCustomerName(String authenticatedCustomerName) {
        this.authenticatedCustomerName = authenticatedCustomerName;
    }
}
