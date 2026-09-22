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
import com.wimp.app.models.Payment;
import com.wimp.app.models.Order;
import com.wimp.app.models.ServiceResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final DataRepository repository;
    private final PaymentGateway paymentGateway;

    public PaymentService(DataRepository repository, PaymentGateway paymentGateway) {
        this.repository = repository;
        this.paymentGateway = paymentGateway;
    }

    public ServiceResult<Payment> authorizePaymentFor(int orderNo) {
        Order order = repository.getOrderByOrderNo(orderNo);
        if (order == null) {
            return ServiceResult.failure("Order " + orderNo + " not found.");
        }

        try {
            Payment payment = paymentGateway.authorize(order.getCustomerEmail(), order.getPrice());
            return ServiceResult.success(payment);
        } catch (Exception ex) {
            return ServiceResult.failure(ex.getMessage());
        }
    }
}
