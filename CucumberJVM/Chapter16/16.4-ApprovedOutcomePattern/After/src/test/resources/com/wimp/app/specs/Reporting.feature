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
    And the restaurant owner is authenticated
    When the sales report is requested for the week beginning 2026-03-09
    Then the report should show:
      """
      * By Pizza
        * BBQ: $2100 (35%)
        * Margherita: $2700 (45%)
        * Pepperoni: $1200 (20%)
      * By Day
        * 2026-03-09: $600 (10%)
        * 2026-03-10: $600 (10%)
        * 2026-03-11: $600 (10%)
        * 2026-03-12: $300 (5%)
        * 2026-03-13: $900 (15%)
        * 2026-03-14: $1800 (30%)
        * 2026-03-15: $1200 (20%)
      * Total: $6000 (100%)
      """
