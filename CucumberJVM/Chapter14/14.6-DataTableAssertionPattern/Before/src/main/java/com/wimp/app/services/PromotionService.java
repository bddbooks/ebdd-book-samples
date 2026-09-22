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
import com.wimp.app.models.MenuItem;
import com.wimp.app.models.OfferedItem;
import com.wimp.app.models.Promotion;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class PromotionService {
    private final DataRepository dataRepository;

    public PromotionService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public Promotion activatePromotion(String promotionName)
    {
        List<OfferedItem> offeredItems = new ArrayList<>();

        if ("Friday $1 off".equals(promotionName)) {
            offeredItems = dataRepository.getMenuItems().stream()
                .sorted(Comparator.comparing(MenuItem::name))
                .filter(item -> item.price().compareTo(BigDecimal.valueOf(8.00)) > 0)
                .map(item -> new OfferedItem(item.name(), item.price().subtract(BigDecimal.ONE), item.price()))
                .toList();
        } else if ("Veggie week".equals(promotionName)) {
            offeredItems = dataRepository.getMenuItems().stream()
                .sorted(Comparator.comparing(MenuItem::name))
                .filter(MenuItem::vegetarian)
                .map(item -> new OfferedItem(item.name(), BigDecimal.valueOf(6.00), item.price()))
                .toList();
        }

        var promotion = new Promotion(promotionName, true,  offeredItems);
        dataRepository.savePromotion(promotion);
        return promotion;
    }

    public boolean isPromotionActive(String promotionName)
    {
        var promotion = dataRepository.getPromotion(promotionName);
        return promotion != null && promotion.isActive();
    }
}
