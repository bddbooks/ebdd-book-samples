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
import com.wimp.app.models.CouponEmail;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
    private final DataRepository dataRepository;

    public EmailService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void sendCouponEmail(String customerName, String couponCode)
    {
        dataRepository.insertCouponEmail(new CouponEmail(customerName, couponCode));
    }

    public boolean wasCouponSent(String customerName, String couponCode) {
        return dataRepository.getCouponEmailsByCustomer(customerName).stream()
            .anyMatch(c -> c.code().equals(couponCode));
    }
}
