package com.wimp.app.specs.support;

import io.cucumber.spring.ScenarioScope;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@ScenarioScope
public class RestApiContext {
    protected static final Logger log = LoggerFactory.getLogger(RestApiContext.class);
    private String bearerToken;

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    // This method must stay public, otherwise it won't be proxied for scenario scope.
    public String getAuthorizationHeader() {
        if (bearerToken != null && !bearerToken.isBlank()) {
            return "Bearer " + bearerToken;
        }
        return null;
    }

    public static void configureRestApiCall(RestApiContext restApiContext, RestClient.Builder clientBuilder) {
        // include Authorization header of the current scenario execution if available
        clientBuilder.requestInitializer(request -> {
            String authorizationHeader = restApiContext.getAuthorizationHeader();
            if (authorizationHeader != null) {
                request.getHeaders().set(HttpHeaders.AUTHORIZATION, authorizationHeader);
            }
        });
        // add interceptor to perform custom logging of the execution
        clientBuilder.requestInterceptor((request, body, execution) -> {
            log.debug("REST API request: {} {}, Headers: {}, Content: {}", request.getMethod(), request.getURI(), request.getHeaders(), peekRequestBodyForLogging(body));
            ClientHttpResponse response = execution.execute(request, body);
            log.debug("REST API response: {}, Headers: {}, Content: {}", response.getStatusCode(), response.getHeaders(), peekResponseBodyForLogging(response));
            return response;
        });
    }

    private static @NonNull String peekRequestBodyForLogging(byte[] body) throws IOException {
        return StreamUtils.copyToString(new ByteArrayInputStream(body), StandardCharsets.UTF_8);
    }

    private static @NonNull String peekResponseBodyForLogging(ClientHttpResponse response) throws IOException {
        var bodyStream = response.getBody();
        var content = StreamUtils.copyToString(bodyStream, StandardCharsets.UTF_8);
        bodyStream.reset();
        return content;
    }
}
