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

import com.wimp.app.models.DailyPizzaSales;
import com.wimp.app.models.Order;
import com.wimp.app.models.OrderStatus;
import com.wimp.app.models.ServiceResult;
import com.wimp.app.services.TestDataService;
import com.wimp.app.config.AppConfigurationProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@Transactional
@Profile("backdoor-api") // only allow backdoor API if profile is enabled
public class TestDataController {
    private final TestDataService testDataService;
    private final AppConfigurationProvider appConfigurationProvider;

    public TestDataController(TestDataService testDataService, AppConfigurationProvider appConfigurationProvider) {
        this.testDataService = testDataService;
        this.appConfigurationProvider = appConfigurationProvider;
    }

    @PostMapping("/prepare-order")
    public ResponseEntity<Order> prepareOrder(
        @RequestParam OrderStatus status,
        @RequestParam String customerName,
        @RequestBody PlaceOrderRequest request
    ) {
        ServiceResult<Order> result = testDataService.prepareTestOrder(
            customerName,
            request,
            status
        );
        if (!result.successful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.errorMessage());
        }

        return ResponseEntity.ok(result.value());
    }

    @PostMapping("/prepare-sales-traffic")
    public ResponseEntity<Void> prepareSalesTraffic(@RequestBody DailyPizzaSales[] salesEntries) {
        ServiceResult<Integer> result = testDataService.prepareSalesTraffic(List.of(salesEntries));
        if (!result.successful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.errorMessage());
        }

        return ResponseEntity.ok().build();
    }
}
