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

import com.wimp.app.models.CurrencyValue;
import com.wimp.app.models.OrderItemPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    protected static final Logger log = LoggerFactory.getLogger(PricingService.class);

    private final MarketService marketService;

    public PricingService(MarketService marketService) {
        this.marketService = marketService;
    }

    public OrderItemPrice calculateItemPrice(CurrencyValue netUnitPrice, int quantity) {
        if (!netUnitPrice.currency().equals(marketService.getCurrency())) {
            throw new IllegalStateException("With this deployment only " + marketService.getCurrency() + " currency calculations can be made");
        }
        var orderItemPrice = new OrderItemPrice(netUnitPrice.multiply(quantity), marketService.getSalesTax());
        log.info("Calculated order item price for {} quantity: {}", quantity, orderItemPrice);
        return orderItemPrice;
    }
}
