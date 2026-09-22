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

import com.wimp.app.models.Customer;
import com.wimp.app.models.ServiceResult;
import com.wimp.app.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@Transactional
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Customer> register(@RequestBody RegisterRequest request) {
        ServiceResult<Customer> result = authenticationService.register(request.customerName(), request.email());
        if (!result.successful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.errorMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.value());
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        ServiceResult<String> result = authenticationService.login(request.customerName(), request.password());
        if (!result.successful()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, result.errorMessage());
        }

        return new LoginResponse(result.value(), request.customerName());
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization) {
        String token = authenticationService.extractToken(authorization);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The request is not authorized for this operation");
        }

        authenticationService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
