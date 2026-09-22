package com.wimp.app.specs.drivers;

import com.wimp.app.models.SalesReport;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.time.LocalDate;

@Component
public class ReportingApiDriver {
    private final ReportingApiClient reportingApiClient;

    @HttpExchange("/api/reporting")
    public interface ReportingApiClient {
        @PostExchange("/sales-report")
        SalesReport generateSalesReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDay);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ReportingApiDriver(ReportingApiClient reportingApiClient) {
        this.reportingApiClient = reportingApiClient;
    }

    public TestAction<SalesReport> generateSalesReport(LocalDate startDay) {
        return new LambdaAction<>("Generate sales report", startDay,
            () -> reportingApiClient.generateSalesReport(startDay));
    }
}
