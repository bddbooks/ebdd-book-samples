Feature: Reporting

Rule: Weekly report can be generated with daily and by pizza breakdown

  Scenario: Weekly sales report shows totals by pizza and day
    Given a normal week of operations with these pizza sales:
      | date       | BBQ  | Pepperoni | Margherita |
      | 2026-03-09 | $100 | $200      | $300       |
      | 2026-03-10 | $300 | $0        | $300       |
      | 2026-03-11 | $200 | $200      | $200       |
      | 2026-03-12 | $0   | $100      | $200       |
      | 2026-03-13 | $200 | $200      | $500       |
      | 2026-03-14 | $700 | $300      | $800       |
      | 2026-03-15 | $600 | $200      | $400       |
    And the restaurant owner is logged in
    When the sales report is requested for the week beginning 2026-03-09
    Then the report should show a total sales volume of $6000
    And there should be $1200 Pepperoni, $2700 Margherita, and $2100 BBQ sales on the report
    And there should be a by day breakdown on the report
    And all values should be also shown as percentages of the total
