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
import com.wimp.app.models.SalesReport;
import com.wimp.app.models.SalesReportValue;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportingService {
    private final DataRepository repository;

    public ReportingService(DataRepository repository) {
        this.repository = repository;
    }

    public SalesReport generateSalesReport(LocalDate startDay) {
        List<DailyPizzaSales> salesData = repository.getDailyPizzaSalesBetweenDates(startDay, startDay.plusDays(6)).stream()
            .sorted(java.util.Comparator.comparing(DailyPizzaSales::getDate))
            .toList();

        BigDecimal totalSales = salesData.stream()
            .map(DailyPizzaSales::getSales)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, SalesReportValue> salesByPizza = new LinkedHashMap<>();
        salesData.stream().map(DailyPizzaSales::getPizza).distinct().forEach(pizza -> {
            BigDecimal sales = salesData.stream()
                .filter(d -> d.getPizza().equals(pizza))
                .map(DailyPizzaSales::getSales)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            salesByPizza.put(pizza, createReportValue(sales, totalSales));
        });

        Map<LocalDate, SalesReportValue> salesByDay = new LinkedHashMap<>();
        salesData.stream().map(DailyPizzaSales::getDate).distinct().forEach(date -> {
            BigDecimal sales = salesData.stream()
                .filter(d -> d.getDate().equals(date))
                .map(DailyPizzaSales::getSales)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            salesByDay.put(date, createReportValue(sales, totalSales));
        });

        return new SalesReport(salesByPizza, salesByDay, createReportValue(totalSales, totalSales));
    }

    private SalesReportValue createReportValue(BigDecimal sales, BigDecimal totalSales) {
        BigDecimal percentage = totalSales.signum() == 0 ? BigDecimal.ZERO : sales.multiply(BigDecimal.valueOf(100)).divide(totalSales, 2, java.math.RoundingMode.HALF_UP);
        return new SalesReportValue(sales, percentage);
    }
}
