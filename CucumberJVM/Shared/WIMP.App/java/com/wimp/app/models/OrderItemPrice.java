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

package com.wimp.app.models;

import java.math.BigDecimal;

public record OrderItemPrice(CurrencyValue netPrice, BigDecimal salesTaxPercent) {
    public CurrencyValue grossPrice() {
        return netPrice.multiply(BigDecimal.ONE.add(salesTaxPercent.divide(BigDecimal.valueOf(100))));
    }

    @Override
    public String toString() {
        return netPrice + " + " + salesTaxPercent + "% TAX";
    }
}
