package com.wimp.specs.drivers;

import com.wimp.app.restapi.LoginRequest;
import com.wimp.app.restapi.LoginResponse;
import com.wimp.specs.support.LambdaAction;
import com.wimp.specs.support.RestApiContext;
import com.wimp.specs.support.TestAction;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationApiDriver {
    private final RestApiContext restApiContext;

    public AuthenticationApiDriver(RestApiContext restApiContext) {
        this.restApiContext = restApiContext;
    }

    public TestAction<LoginResponse> login(String customerName, String password) {
        return new LambdaAction<>(() -> {
            LoginResponse response = restApiContext.processRequest(
                "Login",
                HttpMethod.POST,
                "/api/auth/login",
                new LoginRequest(customerName, password),
                HttpStatus.OK,
                LoginResponse.class);
            restApiContext.setBearerToken(response.token());
            return response;
        });
    }
}
