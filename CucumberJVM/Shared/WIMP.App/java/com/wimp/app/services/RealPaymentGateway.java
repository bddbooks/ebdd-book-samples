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

import com.wimp.app.models.Payment;

/**
 * This class is a placeholder to implement the payment gateway for production
 * usage.
 * <p>
 * Check out the "PaymentGatewaySimulator.SimulatedPaymentGateway" class in the
 * test source code for a stub implementation of this interface.
 *
 * @see com.wimp.app.WimpAutoConfiguration for bean configuration.
 */
public class RealPaymentGateway implements PaymentGateway {
    @Override
    public Payment authorize(String customerEmail, java.math.BigDecimal amount) {
        throw new UnsupportedOperationException("The real payment gateway cannot be used for testing");
    }
}
