package com.wimp.app.specs.drivers;

import com.wimp.app.models.DailyPizzaSales;
import com.wimp.app.models.Order;
import com.wimp.app.models.OrderStatus;
import com.wimp.app.restapi.PlaceOrderRequest;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
import com.wimp.app.specs.support.VoidReturn;
import io.cucumber.datatable.DataTable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Profile("backdoor-api")
public class BackdoorApiDriver {
    private final BackdoorApiClient backdoorApiClient;

    @HttpExchange("/api/test")
    public interface BackdoorApiClient {
        @PostExchange("/prepare-order")
        Order prepareOrder(@RequestParam String customerName, @RequestParam OrderStatus status,
                           @RequestBody PlaceOrderRequest request);

        @PostExchange("/prepare-sales-traffic")
        void prepareSalesTraffic(@RequestBody DailyPizzaSales[] salesData);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public BackdoorApiDriver(BackdoorApiClient backdoorApiClient) {
        this.backdoorApiClient = backdoorApiClient;
    }

    public TestAction<Order> prepareOrder(String customerName, PlaceOrderRequest orderRequest, OrderStatus status) {
        return new LambdaAction<>("Prepare test order", "%s/%s/%s".formatted(customerName, orderRequest, status), () ->
            backdoorApiClient.prepareOrder(customerName, status, orderRequest));
    }

    public TestAction<VoidReturn> prepareSalesTraffic(List<DailyPizzaSales> salesData) {
        return new LambdaAction.Void("Prepare sales traffic", salesData, () ->
            backdoorApiClient.prepareSalesTraffic(salesData.toArray(new DailyPizzaSales[0])));
    }

    public TestAction<VoidReturn> prepareSalesTraffic(DataTable salesTable) {
        List<Map<String, String>> rows = salesTable.asMaps();
        List<DailyPizzaSales> salesTraffic = new ArrayList<>();
        for (Map<String, String> row : rows) {
            LocalDate date = LocalDate.parse(row.get("date"));
            for (Map.Entry<String, String> cell : row.entrySet()) {
                if (cell.getKey().equals("date")) {
                    continue;
                }
                DailyPizzaSales sales = new DailyPizzaSales();
                sales.setDate(date);
                sales.setPizza(cell.getKey());
                sales.setSales(cell.getValue() == null || cell.getValue().isEmpty()
                    ? BigDecimal.ZERO
                    : parseMoneyValue(cell.getValue()));
                salesTraffic.add(sales);
            }
        }

        return prepareSalesTraffic(salesTraffic);
    }

    private static BigDecimal parseMoneyValue(String value) {
        return new BigDecimal(value.replaceFirst("^\\$", ""));
    }
}
