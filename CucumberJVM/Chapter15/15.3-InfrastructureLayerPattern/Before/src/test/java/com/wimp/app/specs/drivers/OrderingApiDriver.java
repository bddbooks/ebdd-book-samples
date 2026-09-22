package com.wimp.app.specs.drivers;

import com.wimp.app.models.Order;
import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.app.specs.support.RestApiContext;
import com.wimp.app.specs.support.TestActionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

@Component
public class OrderingApiDriver {

    private final RestTestClient restTestClient;
    private final RestApiContext restApiContext;

    public OrderingApiDriver(WebApplicationContext context, RestApiContext restApiContext) {
        this.restTestClient = RestTestClient.bindToApplicationContext(context).build();
        this.restApiContext = restApiContext;
    }

    public Order placeOrder(PlaceOrderRequest placeOrderRequest) {
        try {
            var response = restTestClient.post().uri("/api/orders")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + restApiContext.getBearerToken())
                .body(placeOrderRequest)
                .exchange();
            response.expectStatus().is2xxSuccessful();
            return Optional.ofNullable(response.expectBody(Order.class).returnResult().getResponseBody())
                .orElseThrow(() -> new RuntimeException("No result payload found"));
        } catch (Exception ex) {
            throw new TestActionFailedException(ex.getMessage());
        }
    }

    public void cancelOrder(int orderNo) {
        try {
            var response = restTestClient.delete().uri("/api/orders/{orderNo}", orderNo)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + restApiContext.getBearerToken())
                .exchange();
            response.expectStatus().is2xxSuccessful();
        } catch (Exception ex) {
            throw new TestActionFailedException(ex.getMessage());
        }
    }
}
