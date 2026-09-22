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
import com.wimp.app.models.Coupon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {
    private final DataRepository repository;

    public EmailService(DataRepository repository) {
        this.repository = repository;
    }

    public void sendCoupon(String customerEmail, String couponCode) {
        Coupon coupon = new Coupon();
        coupon.setCustomerEmail(customerEmail);
        coupon.setCode(couponCode);
        repository.insertCoupon(coupon);
    }

    public boolean wasCouponSent(String customerEmail, String couponCode) {
        return repository.getCouponsByEmail(customerEmail).stream()
            .anyMatch(c -> c.getCode().equalsIgnoreCase(couponCode));
    }

    public List<Coupon> getCoupons(String customerEmail) {
        return repository.getCouponsByEmail(customerEmail);
    }
}
