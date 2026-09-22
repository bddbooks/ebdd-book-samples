package com.wimp.app.specs.drivers;

import com.wimp.app.models.ContactDetails;
import com.wimp.app.models.Order;
import com.wimp.app.models.OrderCollectionDetails;
import com.wimp.app.restapi.ChangeAddressRequest;
import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
import com.wimp.app.specs.support.VoidReturn;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import org.springframework.web.service.annotation.GetExchange;

@Component
public class OrderingApiDriver {
    private final OrderingApiClient orderingApiClient;

    @HttpExchange("/api/orders")
    public interface OrderingApiClient {
        @GetExchange("/{orderNo}")
        Order getOrder(@PathVariable int orderNo);

        @PostExchange
        Order placeOrder(@RequestBody PlaceOrderRequest request);

        @PutExchange("/{orderNo}/delivery-address")
        void changeDeliveryAddress(@PathVariable int orderNo, @RequestBody ChangeAddressRequest request);

        @PutExchange("/{orderNo}/customer-collection")
        OrderCollectionDetails setForCollection(@PathVariable int orderNo);

        @PutExchange("/{orderNo}/contact-details")
        void provideContactDetails(@PathVariable int orderNo, @RequestBody ContactDetails details);

        @DeleteExchange("/{orderNo}")
        void cancelOrder(@PathVariable int orderNo);

        @PostExchange("/{orderNo}/deliver")
        Order deliverOrder(@PathVariable int orderNo);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public OrderingApiDriver(OrderingApiClient orderingApiClient) {
        this.orderingApiClient = orderingApiClient;
    }

    public TestAction<Order> getOrder(int orderNo) {
        return new LambdaAction<>("Get order", orderNo, () -> orderingApiClient.getOrder(orderNo));
    }

    public TestAction<Order> placeOrder(PlaceOrderRequest placeOrderRequest) {
        return new LambdaAction<>("Place order", placeOrderRequest, () -> orderingApiClient.placeOrder(placeOrderRequest));
    }

    public TestAction<VoidReturn> changeDeliveryAddress(int orderNo, ChangeAddressRequest changeAddressRequest) {
        return new LambdaAction.Void("Change delivery address", "%d/%s".formatted(orderNo, changeAddressRequest), () ->
            orderingApiClient.changeDeliveryAddress(orderNo, changeAddressRequest));
    }

    public TestAction<OrderCollectionDetails> setForCollection(int orderNo) {
        return new LambdaAction<>("Set for customer-collection", orderNo, () -> orderingApiClient.setForCollection(orderNo));
    }

    public TestAction<VoidReturn> provideContactDetails(int orderNo, ContactDetails contactDetails) {
        return new LambdaAction.Void("Provide contact details", "%d/%s".formatted(orderNo, contactDetails), () ->
            orderingApiClient.provideContactDetails(orderNo, contactDetails));
    }

    public TestAction<VoidReturn> cancelOrder(int orderNo) {
        return new LambdaAction.Void("Cancel order", orderNo, () ->
            orderingApiClient.cancelOrder(orderNo));
    }

    public TestAction<Order> deliverOrder(int orderNo) {
        return new LambdaAction<>("Deliver order", orderNo, () -> orderingApiClient.deliverOrder(orderNo));
    }
}
