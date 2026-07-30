Feature: Reporting

Rule: Weekly sales report can be generated with daily and by pizza breakdown

  Scenario: Weekly sales report provided for a realistic traffic
    Given sales traffic from "realistic-traffic.csv"
    And the restaurant owner is authenticated
    When the sales report is requested for the week beginning 2026-03-09
    Then the report should show:
      """
      * By Pizza
        * BBQ: $2100 (26%)
        * Margherita: $2700 (34%)
        * Pepperoni: $1200 (15%)
        * Truffle Bliss: $2000 (25%)
      * By Day
        * 2026-03-09: $800 (10%)
        * 2026-03-10: $700 (9%)
        * 2026-03-11: $700 (9%)
        * 2026-03-12: $500 (6%)
        * 2026-03-13: $1200 (15%)
        * 2026-03-14: $2300 (29%)
        * 2026-03-15: $1800 (23%)
      * Total: $8000 (100%)
      """

Rule: Ingredient usage report can be generated for a week

  Scenario: No sale of a particular ingredient on a week
    Given sales traffic from "realistic-traffic.csv" with
      | exclude section|
      | truffle sales  |
    And the restaurant owner is authenticated
    When the ingredient usage report is requested for the week beginning 2026-03-09
    Then the ingredient usage report should contain "truffle" usage as 0 portions
