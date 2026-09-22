package com.wimp.app.specs.drivers;

import com.wimp.app.restapi.ErrorResponse;
import com.wimp.app.restapi.LoginRequest;
import com.wimp.app.restapi.LoginResponse;
import com.wimp.app.specs.support.RestApiContext;
import com.wimp.app.specs.support.TestActionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;
import java.util.Optional;

@Component
public class AuthenticationApiDriver {

    private final RestApiContext restApiContext;
    private final RestTestClient restTestClient;

    public AuthenticationApiDriver(RestApiContext restApiContext, WebApplicationContext context) {
        this.restApiContext = restApiContext;
        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
    }

    public LoginResponse login(String customerName, String password) {
        try {
            var response = restTestClient.post().uri("/api/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest(customerName, password))
                .exchange();
            if (response.returnResult().getStatus() != HttpStatus.OK) {
                throw new RuntimeException("Request failed with status %s. Error message: %s".formatted(response.returnResult().getStatus(), Objects.requireNonNull(response.expectBody(ErrorResponse.class).returnResult().getResponseBody()).error()));
            }
            LoginResponse loginResponse =
                Optional.ofNullable(response.expectBody(LoginResponse.class).returnResult().getResponseBody())
                    .orElseThrow(() -> new RuntimeException("No result payload found"));
            restApiContext.setBearerToken(loginResponse.token());
            return loginResponse;
        } catch (Exception ex) {
            throw new TestActionFailedException(ex.getMessage());
        }
    }
}
