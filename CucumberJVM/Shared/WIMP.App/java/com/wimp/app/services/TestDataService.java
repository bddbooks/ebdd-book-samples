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
import com.wimp.app.models.DailyPizzaSales;
import com.wimp.app.models.Order;
import com.wimp.app.models.OrderStatus;
import com.wimp.app.models.ServiceResult;
import com.wimp.app.config.AppConfigurationProvider;

import com.wimp.app.restapi.PlaceOrderRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("backdoor-api") // only allow backdoor API if profile is enabled
public class TestDataService {
    private final DataRepository repository;
    private final OrderService orderService;
    private final AppConfigurationProvider appConfigurationProvider;

    public TestDataService(DataRepository repository, OrderService orderService, AppConfigurationProvider appConfigurationProvider) {
        this.repository = repository;
        this.orderService = orderService;
        this.appConfigurationProvider = appConfigurationProvider;
    }

    public ServiceResult<Order> prepareTestOrder(
        String customerName,
        PlaceOrderRequest placeOrderRequest,
        OrderStatus status
    ) {
        ServiceResult<Order> result = orderService.placeOrder(customerName, placeOrderRequest);
        if (!result.successful()) {
            return result;
        }

        Order order = repository.updateOrder(result.value(), status, null, null, null);
        return ServiceResult.success(order);
    }

    public ServiceResult<Integer> prepareSalesTraffic(List<DailyPizzaSales> salesEntries) {
        if (appConfigurationProvider.simulation().bug()) {
            return ServiceResult.failure("Simulated error");
        }

        repository.insertDailyPizzaSalesEntries(salesEntries);
        return ServiceResult.success(salesEntries.size());
    }
}
