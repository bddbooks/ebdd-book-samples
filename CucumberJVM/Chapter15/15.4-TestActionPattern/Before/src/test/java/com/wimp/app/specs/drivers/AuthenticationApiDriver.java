package com.wimp.app.specs.drivers;

import com.wimp.app.restapi.LoginRequest;
import com.wimp.app.restapi.LoginResponse;
import com.wimp.app.restapi.RegisterRequest;
import com.wimp.app.specs.support.RestApiContext;
import com.wimp.app.specs.support.TestActionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
public class AuthenticationApiDriver {

    protected static final Logger log = LoggerFactory.getLogger(AuthenticationApiDriver.class);

    private final RestApiContext restApiContext;
    private final AuthenticationApiClient authenticationApiClient;

    @HttpExchange("/api/auth")
    public interface AuthenticationApiClient {
        @PostExchange("login")
        LoginResponse login(@RequestBody LoginRequest request);

        @PostExchange("register")
        void register(@RequestBody RegisterRequest request);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public AuthenticationApiDriver(RestApiContext restApiContext, AuthenticationApiClient authenticationApiClient) {
        this.restApiContext = restApiContext;
        this.authenticationApiClient = authenticationApiClient;
    }

    public LoginResponse login(String customerName, String password) {
        log.info("Executing Login...");
        long startTime = System.nanoTime();
        try {
            LoginResponse response = authenticationApiClient.login(new LoginRequest(customerName, password));
            log.info("Login executed successfully in {}", (System.nanoTime() - startTime) / 1_000_000);
            restApiContext.setBearerToken(response.token());
            return response;
        } catch (Exception ex) {
            log.error("Login failed: {}", ex.getMessage());
            throw new TestActionFailedException(ex.getMessage());
        }
    }

    public void register(String customerName, String email) {
        log.info("Executing Register...");
        long startTime = System.nanoTime();
        try {
            authenticationApiClient.register(new RegisterRequest(customerName, email));
            log.info("Register executed successfully in {}", (System.nanoTime() - startTime) / 1_000_000);
        } catch (Exception ex) {
            log.error("Register failed: {}", ex.getMessage());
            throw new TestActionFailedException(ex.getMessage());
        }
    }
}
