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

public record CurrencyValue(BigDecimal value, String currency) implements Comparable<CurrencyValue> {
    @Override
    public String toString() {
        return currency + " " + value.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public CurrencyValue multiply(int quantity) {
        return new CurrencyValue(value.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    public CurrencyValue multiply(BigDecimal multiplier) {
        return new CurrencyValue(value.multiply(multiplier), currency);
    }

    public CurrencyValue add(CurrencyValue other) {
        if (!currency.equals(other.currency())) {
            throw new IllegalStateException("Currencies must be the same for addition");
        }
        return new CurrencyValue(value.add(other.value()), currency);
    }

    @Override
    public int compareTo(CurrencyValue other) {
        if (!currency.equals(other.currency())) {
            throw new IllegalStateException("Currencies must be the same for comparison");
        }
        return this.value.compareTo(other.value);
    }
}
