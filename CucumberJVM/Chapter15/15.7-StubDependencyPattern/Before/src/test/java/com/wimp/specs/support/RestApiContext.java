package com.wimp.specs.support;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonSerializer;
import com.wimp.app.restapi.ErrorResponse;
import io.cucumber.spring.ScenarioScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@ScenarioScope
public class RestApiContext {
    private final MockMvc mockMvc;
    private final Gson gson;
    private String bearerToken;

    public RestApiContext(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public void clearBearerToken() {
        this.bearerToken = null;
    }

    public <TResult> TResult getRequest(String path, Class<TResult> responseType) throws Exception {
        return processRequest("GET " + path, HttpMethod.GET, path, null, HttpStatus.OK, responseType);
    }

    public <TResult> TResult processRequest(
        String actionName,
        HttpMethod method,
        String path,
        Object payload,
        HttpStatus successStatusCode,
        Class<TResult> responseType
    ) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.request(method, path);
        if (bearerToken != null && !bearerToken.isBlank()) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        }
        if (payload != null) {
            requestBuilder.contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(payload));
        }

        MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();
        if (response.getStatus() != successStatusCode.value()) {
            throw new TestActionFailedException(actionName + " failed with status code " + response.getStatus()
                + ". Error message: '" + readErrorMessage(response) + "'");
        }

        if (responseType == VoidReturn.class) {
            return responseType.cast(VoidReturn.INSTANCE);
        }

        return gson.fromJson(response.getContentAsString(), responseType);
    }

    private String readErrorMessage(MockHttpServletResponse response) throws Exception {
        String responseText = response.getContentAsString();
        if (responseText == null || responseText.isBlank()) {
            return "n/a";
        }

        try {
            ErrorResponse errorResponse = gson.fromJson(responseText, ErrorResponse.class);
            if (errorResponse.error() != null && !errorResponse.error().isBlank()) {
                return errorResponse.error();
            }
        } catch (JsonSyntaxException ignored) {
            // ignored on purpose: if it is not a valid error payload, return raw response text
        }

        return responseText;
    }
}
