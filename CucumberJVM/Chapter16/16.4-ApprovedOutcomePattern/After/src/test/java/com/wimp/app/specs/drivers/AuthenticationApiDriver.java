package com.wimp.app.specs.drivers;

import com.wimp.app.restapi.LoginRequest;
import com.wimp.app.restapi.LoginResponse;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.RestApiContext;
import com.wimp.app.specs.support.TestAction;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
public class AuthenticationApiDriver {
    private final RestApiContext restApiContext;
    private final AuthenticationApiClient authenticationApiClient;

    @HttpExchange("/api/auth")
    public interface AuthenticationApiClient {
        @PostExchange("login")
        LoginResponse login(@RequestBody LoginRequest request);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public AuthenticationApiDriver(RestApiContext restApiContext, AuthenticationApiClient authenticationApiClient) {
        this.restApiContext = restApiContext;
        this.authenticationApiClient = authenticationApiClient;
    }

    public TestAction<LoginResponse> login(String customerName, String password) {
        return new LambdaAction<>("Login", "%s/%s".formatted(customerName, password), () -> {
            LoginResponse response = authenticationApiClient.login(new LoginRequest(customerName, password));
            restApiContext.setBearerToken(response.token());
            return response;
        });
    }
}
