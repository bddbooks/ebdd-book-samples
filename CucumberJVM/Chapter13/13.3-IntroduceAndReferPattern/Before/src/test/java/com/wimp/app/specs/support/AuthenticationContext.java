package com.wimp.app.specs.support;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class AuthenticationContext {
    private String authenticatedCustomerName;

    public String getAuthenticatedCustomerName() {
        return authenticatedCustomerName;
    }

    public void setAuthenticatedCustomerName(String authenticatedCustomerName) {
        this.authenticatedCustomerName = authenticatedCustomerName;
    }
}
