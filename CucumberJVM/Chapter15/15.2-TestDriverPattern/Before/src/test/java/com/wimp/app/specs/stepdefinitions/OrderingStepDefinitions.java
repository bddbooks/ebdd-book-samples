package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.Order;
import com.wimp.app.specs.support.PlaceOrderRequestObjectMother;
import com.wimp.app.specs.support.RestApiContext;
import io.cucumber.java.en.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

public class OrderingStepDefinitions {
    private final RestApiContext restApiContext;
    private final RestTestClient restTestClient;

    private Integer placedOrderNo;

    public OrderingStepDefinitions(RestApiContext restApiContext, WebApplicationContext context) {
        this.restApiContext = restApiContext;
        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
    }

    @Given("they have placed an order")
    public void theyHavePlacedAnOrder() throws Exception {
        var placeOrderRequest = new PlaceOrderRequestObjectMother().build();
        var response = restTestClient.post().uri("/api/orders")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + restApiContext.getBearerToken())
            .body(placeOrderRequest)
            .exchange();
        response.expectStatus().is2xxSuccessful();
        var placedOrder = Optional.ofNullable(response.expectBody(Order.class).returnResult().getResponseBody())
            .orElseThrow(() -> new RuntimeException("No result payload found"));
        placedOrderNo = placedOrder.getOrderNo();
    }

    @When("they cancel the placed order")
    public void theyCancelTheOrder() throws Exception {
        int orderNo = Optional.ofNullable(placedOrderNo).orElseThrow(() -> new RuntimeException("No placed order"));
        var response = restTestClient.delete().uri("/api/orders/{orderNo}", orderNo)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + restApiContext.getBearerToken())
            .exchange();
        response.expectStatus().is2xxSuccessful();
    }
}
