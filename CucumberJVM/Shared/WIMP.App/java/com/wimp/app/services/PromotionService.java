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

import com.wimp.app.data.DataRepository;
import com.wimp.app.models.Promotion;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {
    private final DataRepository repository;

    public PromotionService(DataRepository repository) {
        this.repository = repository;
    }

    public Promotion activatePromotion(String name) {
        Promotion existing = repository.getPromotionByName(name);
        if (existing != null) {
            return repository.updatePromotion(existing, true);
        }

        Promotion promotion = new Promotion();
        promotion.setName(name);
        promotion.setActive(true);
        repository.insertPromotion(promotion);
        return promotion;
    }

    public boolean isPromotionActive(String name) {
        Promotion promotion = repository.getPromotionByName(name);
        return promotion != null && promotion.isActive();
    }
}
