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

package com.wimp.app.restapi;

import com.wimp.app.models.Coupon;
import com.wimp.app.services.EmailService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@Transactional
public class CouponsController {
    private final EmailService emailService;

    public CouponsController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/{customerEmail}")
    public List<Coupon> getCoupons(@PathVariable String customerEmail) {
        return emailService.getCoupons(customerEmail);
    }
}
