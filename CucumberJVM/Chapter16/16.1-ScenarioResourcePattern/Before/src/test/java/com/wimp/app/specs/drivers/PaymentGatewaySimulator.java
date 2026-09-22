package com.wimp.app.specs.drivers;

import com.wimp.app.models.Payment;
import com.wimp.app.services.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentGatewaySimulator {
    private static final Logger log = LoggerFactory.getLogger(PaymentGatewaySimulator.class);

    public SimulatedPaymentGateway start() {
        return new SimulatedPaymentGateway();
    }

    /**
     * This class demonstrates the behavior of an externally provided stub
     * service.
     */
    public static class SimulatedPaymentGateway implements PaymentGateway {
        private static volatile boolean isRunning;

        private final List<Payment> payments = new ArrayList<>();
        private boolean isDisposed;

        public SimulatedPaymentGateway() {
            if (isRunning) {
                throw new IllegalStateException("The payment gateway simulator has been started already");
            }

            log.info("Starting Payment Gateway simulator...");
            try {
                Thread.sleep(500); // simulating slow start
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            isRunning = true;
        }

        @Override
        public Payment authorize(String customerEmail, BigDecimal amount) {
            assertNotDisposed();

            Payment payment = new Payment();
            payment.setAmount(amount);
            payment.setCustomerEmail(customerEmail);
            payment.setPaymentReference(UUID.randomUUID().toString().replace("-", ""));
            if ("nofunds@example.com".equals(customerEmail)) {
                log.info("Simulating failed authorization...");
                payment.setSuccess(false);
                payment.setMessage("Insufficient funds");
            } else {
                log.info("Simulating successful authorization...");
                payment.setSuccess(true);
                payment.setMessage("OK");
            }

            payments.add(payment);
            return payment;
        }

        public Optional<Payment> getPaymentByReference(String paymentReference) {
            return payments.stream()
                .filter(p -> p.getPaymentReference().equals(paymentReference))
                .findFirst();
        }

        public void stop() {
            assertNotDisposed();
            isDisposed = true;
            if (!isRunning) {
                throw new IllegalStateException("The payment gateway simulator was not running");
            }
            isRunning = false;
        }

        private void assertNotDisposed() {
            if (isDisposed) {
                throw new IllegalStateException("The payment gateway simulator has been disposed");
            }
        }
    }
}
