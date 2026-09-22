package com.wimp.app.specs.drivers;

import com.wimp.app.models.Payment;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
public class PaymentApiDriver {
    private final PaymentApiClient paymentApiClient;

    @HttpExchange("/api/payment")
    public interface PaymentApiClient {
        @PostExchange("/authorize")
        Payment authorize(@RequestParam int orderNo);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public PaymentApiDriver(PaymentApiClient paymentApiClient) {
        this.paymentApiClient = paymentApiClient;
    }

    public TestAction<Payment> authorizePaymentFor(int orderNo) {
        return new LambdaAction<>("Authorize payment", orderNo, () -> paymentApiClient.authorize(orderNo));
    }
}
