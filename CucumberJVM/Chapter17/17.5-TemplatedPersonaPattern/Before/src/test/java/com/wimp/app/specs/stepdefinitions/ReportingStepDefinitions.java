package com.wimp.app.specs.stepdefinitions;

import com.wimp.app.models.DailyPizzaSales;
import com.wimp.app.models.SalesReport;
import com.wimp.app.specs.drivers.BackdoorApiDriver;
import com.wimp.app.specs.drivers.ReportingApiDriver;
import com.wimp.app.specs.support.ReportPrinter;
import com.wimp.app.specs.support.TestFileSystem;
import io.cucumber.java.en.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportingStepDefinitions {
    private final BackdoorApiDriver backdoorApiDriver;
    private final ReportingApiDriver reportingApiDriver;
    private final TestFileSystem testFileSystem;
    private SalesReport generatedReport;

    public ReportingStepDefinitions(BackdoorApiDriver backdoorApiDriver, ReportingApiDriver reportingApiDriver, TestFileSystem testFileSystem) {
        this.backdoorApiDriver = backdoorApiDriver;
        this.reportingApiDriver = reportingApiDriver;
        this.testFileSystem = testFileSystem;
    }

    @Given("sales traffic from {string}")
    public void salesTrafficFrom(String fileName) throws Exception {
        loadTrafficData(fileName);
    }

    private void loadTrafficData(String fileName) throws Exception {
        // A detailed discussion of the TestFileSystem class can be found in chapter 21: Test File System pattern
        Path path = Path.of(testFileSystem.getFeatureInputFolder(), fileName);
        List<String> lines = Files.readAllLines(path);
        if (lines.size() < 2) throw new IllegalArgumentException("The CSV file '" + path + "' was empty!");
        var headers = Arrays.asList(lines.getFirst().split(","));
        List<DailyPizzaSales> sales = new ArrayList<>();
        for (String line : lines.subList(1, lines.size())) {
            String[] values = line.split(",");
            String pizzaName = values[headers.indexOf("Pizza")];
            DailyPizzaSales item = new DailyPizzaSales();
            item.setDate(LocalDate.parse(values[headers.indexOf("Date")]));
            item.setPizza(pizzaName);
            item.setSales(new BigDecimal(values[headers.indexOf("Sales")]));
            sales.add(item);
        }
        backdoorApiDriver.prepareSalesTraffic(sales).execute();
    }

    @When("the sales report is requested for the week beginning {date}")
    public void theSalesReportIsRequestedForTheWeekBeginning(LocalDate startDay) throws Exception {
        generatedReport = reportingApiDriver.generateSalesReport(startDay).execute();
    }

    @Then("the report should show:")
    public void theReportShouldShow(String expectedReport) {
        assertThat(ReportPrinter.printReport(generatedReport).trim().lines().toList())
            .as("The printed report did not match the expected output.")
            .containsExactlyElementsOf(expectedReport.trim().lines().toList());
    }

    @When("the ingredient usage report is requested for the week beginning {date}")
    public void theIngredientUsageReportIsRequestedForTheWeekBeginning(LocalDate startDay) {
      // ingredient usage report generation is not part of this sample
    }

    @Then("the ingredient usage report should contain {string} usage as {int} portions")
    public void theIngredientUsageReportShouldContainUsageAsPortions(String ingredient, int portions) {
      // ingredient usage report generation is not part of this sample
    }
}
