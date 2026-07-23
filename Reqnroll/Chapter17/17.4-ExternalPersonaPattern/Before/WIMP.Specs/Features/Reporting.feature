Feature: Reporting

Rule: Weekly sales report can be generated with daily and by pizza breakdown

  Scenario: Weekly sales report shows totals by pizza and day
    Given a week of operations with these pizza sales:
      | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
      | 2026-03-09 | $100 | $200      | $300       | $200          |
      | 2026-03-10 | $300 | $0        | $300       | $100          |
      | 2026-03-11 | $200 | $200      | $200       | $100          |
      | 2026-03-12 | $0   | $100      | $200       | $200          |
      | 2026-03-13 | $200 | $200      | $500       | $300          |
      | 2026-03-14 | $700 | $300      | $800       | $500          |
      | 2026-03-15 | $600 | $200      | $400       | $600          |
    And the restaurant owner is logged in
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
