package com.wimp.app.specs.drivers;

import com.wimp.app.models.Coupon;
import com.wimp.app.models.Promotion;
import com.wimp.app.restapi.ActivatePromotionRequest;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@Component
public class PromotionsApiDriver {
    private final PromotionsApiClient promotionsApiClient;

    @HttpExchange("/api")
    public interface PromotionsApiClient {
        @PostExchange("/promotions")
        Promotion activatePromotion(@RequestBody ActivatePromotionRequest request);

        @GetExchange("/coupons/{customerEmail}")
        Coupon[] getCoupons(@PathVariable String customerEmail);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public PromotionsApiDriver(PromotionsApiClient promotionsApiClient) {
        this.promotionsApiClient = promotionsApiClient;
    }

    public TestAction<Promotion> activatePromotion(ActivatePromotionRequest activatePromotionRequest) {
        return new LambdaAction<>("Activate promotion", activatePromotionRequest,
            () -> promotionsApiClient.activatePromotion(activatePromotionRequest));
    }

    public TestAction<List<Coupon>> getCoupons(String customerEmail) {
        return new LambdaAction<>("Get coupons", customerEmail,
            () -> List.of(promotionsApiClient.getCoupons(customerEmail)));
    }
}
