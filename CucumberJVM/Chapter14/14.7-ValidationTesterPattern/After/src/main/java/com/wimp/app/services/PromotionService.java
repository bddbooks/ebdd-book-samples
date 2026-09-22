/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

package com.wimp.app.services;

import com.wimp.app.infrastructure.DataRepository;
import com.wimp.app.models.Promotion;
import org.springframework.stereotype.Component;

@Component
public class PromotionService {
    private final DataRepository dataRepository;

    public PromotionService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void activatePromotion(String promotionName)
    {
        var promotion = new Promotion(promotionName, true);
        dataRepository.savePromotion(promotion);
    }

    public boolean isPromotionActive(String promotionName)
    {
        var promotion = dataRepository.getPromotion(promotionName);
        return promotion != null && promotion.isActive();
    }
}
