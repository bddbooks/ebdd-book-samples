package com.wimp.app.specs.support;

import com.wimp.app.specs.drivers.AuthenticationApiDriver;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ScenarioScope
public class AuthenticationContext {
    private final AuthenticationApiDriver authenticationApiDriver;

    private String authenticatedCustomerName;

    public AuthenticationContext(AuthenticationApiDriver authenticationApiDriver) {
        this.authenticationApiDriver = authenticationApiDriver;
    }

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

    public void ensureAuthenticatedCustomer() throws Exception
    {
        if (authenticatedCustomerName == null)
        {
            authenticationApiDriver.login(DomainDefaults.CUSTOMER_NAME, DomainDefaults.PASSWORD).execute();
            authenticatedCustomerName = DomainDefaults.CUSTOMER_NAME;
        }
    }
}
