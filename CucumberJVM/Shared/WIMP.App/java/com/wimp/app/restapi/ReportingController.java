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

import com.wimp.app.models.SalesReport;
import com.wimp.app.services.AuthenticationService;
import com.wimp.app.services.ReportingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reporting")
public class ReportingController {
    private final AuthenticationService authenticationService;
    private final ReportingService reportingService;

    public ReportingController(AuthenticationService authenticationService, ReportingService reportingService) {
        this.authenticationService = authenticationService;
        this.reportingService = reportingService;
    }

    @PostMapping("/sales-report")
    public ResponseEntity<SalesReport> generateSalesReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDay,
        @RequestHeader("Authorization") String authorization
    ) {
        String customerName = requireOwner(authorization);
        if (!customerName.startsWith("Owner")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The request is not authorized for this operation");
        }

        return ResponseEntity.ok(reportingService.generateSalesReport(startDay));
    }

    private String requireOwner(String authorization) {
        String token = authenticationService.extractToken(authorization);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The request is not authorized for this operation");
        }

        String customerName = authenticationService.getCustomerName(token);
        if (customerName == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The request is not authorized for this operation");
        }

        return customerName;
    }
}
