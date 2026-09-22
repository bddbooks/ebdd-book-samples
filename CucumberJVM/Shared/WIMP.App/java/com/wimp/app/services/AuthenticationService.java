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
import com.wimp.app.models.Customer;
import com.wimp.app.models.Session;
import com.wimp.app.models.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * A simple authentication service implementation for the sake of
 * demonstration.
 * <p>
 * Warning: Not intended to use in real applications! The provided
 * implementation is to be able to understand the core concept of authentication
 * with tokens. It is incomplete, uses hard-coded details and does not handle
 * special situations.
 * <p>
 * With Spring, the recommended solution is to use Spring Security. See
 * https://spring.io/projects/spring-security for details.
 */
@Service
public class AuthenticationService {
    private static final String ACCEPTED_PASSWORD = "Pa22w0rd!";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final DataRepository repository;
    private final MessageService messageService;

    public AuthenticationService(DataRepository repository, MessageService messageService) {
        this.repository = repository;
        this.messageService = messageService;
    }

    public ServiceResult<Customer> register(String customerName, String email) {
        if (customerName == null || customerName.isBlank()) {
            return ServiceResult.failure("Customer name is required.");
        }

        if (email == null || email.isBlank() || !isValidEmail(email)) {
            return ServiceResult.failure("A valid email address is required.");
        }

        if (repository.getCustomerByName(customerName) != null) {
            return ServiceResult.failure("A customer named '" + customerName + "' is already registered.");
        }

        Customer customer = new Customer(customerName, email);
        repository.insertCustomer(customer);
        return ServiceResult.success(customer);
    }

    public ServiceResult<String> login(String customerName, String password) {
        if (customerName == null || customerName.isBlank()) {
            return ServiceResult.failure("Customer name is required.");
        }

        if (!ACCEPTED_PASSWORD.equals(password)) {
            return ServiceResult.failure(messageService.getMessage("en-US", "invalid-password"));
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        repository.insertSession(new Session(token, customerName));
        log.info("{} authenticated", customerName);
        return ServiceResult.success(token);
    }

    public void logout(String token) {
        repository.deleteSession(token);
    }

    public String getCustomerName(String token) {
        Session session = repository.getSessionByToken(token);
        return session == null ? null : session.customerName();
    }

    public String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return null;
        }

        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    private static boolean isValidEmail(String email) {
        return email.contains("@") && email.indexOf('@') > 0 && email.lastIndexOf('.') > email.indexOf('@');
    }
}
