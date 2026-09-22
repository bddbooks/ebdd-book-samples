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

  Scenario: Christmas week sales report includes the holiday closure day
    Given a week of operations with these pizza sales:
      | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
      | 2025-12-22 | $100 | $200      | $300       | $200          |
      | 2025-12-23 | $300 | $0        | $300       | $100          |
      | 2025-12-24 | $200 | $200      | $200       | $100          |
      | 2025-12-25 | $0   | $0        | $0         | $0            |
      | 2025-12-26 | $200 | $200      | $500       | $300          |
      | 2025-12-27 | $700 | $300      | $800       | $500          |
      | 2025-12-28 | $600 | $200      | $400       | $600          |
    And the restaurant owner is authenticated
    When the sales report is requested for the week beginning 2025-12-22
    Then the report should show:
      """
      * By Pizza
        * BBQ: $2100 (28%)
        * Margherita: $2500 (33%)
        * Pepperoni: $1100 (15%)
        * Truffle Bliss: $1800 (24%)
      * By Day
        * 2025-12-22: $800 (11%)
        * 2025-12-23: $700 (9%)
        * 2025-12-24: $700 (9%)
        * 2025-12-25: $0 (0%)
        * 2025-12-26: $1200 (16%)
        * 2025-12-27: $2300 (31%)
        * 2025-12-28: $1800 (24%)
      * Total: $7500 (100%)
      """

  Scenario: Truffle shortage sales report has no truffle sales
    Given a week of operations with these pizza sales:
      | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
      | 2026-03-09 | $100 | $200      | $300       | $0            |
      | 2026-03-10 | $300 | $0        | $300       | $0            |
      | 2026-03-11 | $200 | $200      | $200       | $0            |
      | 2026-03-12 | $0   | $100      | $200       | $0            |
      | 2026-03-13 | $200 | $200      | $500       | $0            |
      | 2026-03-14 | $700 | $300      | $800       | $0            |
      | 2026-03-15 | $600 | $200      | $400       | $0            |
    And the restaurant owner is authenticated
    When the sales report is requested for the week beginning 2026-03-09
    Then the report should show:
      """
      * By Pizza
        * BBQ: $2100 (35%)
        * Margherita: $2700 (45%)
        * Pepperoni: $1200 (20%)
        * Truffle Bliss: $0 (0%)
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

Rule: Ingredient usage report can be generated for a week

  Scenario: Weekly sales report shows ingredients usage
    # Uses the same sales data as the scenario 'Weekly sales report shows totals by pizza and day'
    Given a week of operations with these pizza sales:
      | date       | BBQ  | Pepperoni | Margherita | Truffle Bliss |
      | 2026-03-09 | $100 | $200      | $300       | $200          |
      | 2026-03-10 | $300 | $0        | $300       | $100          |
      | 2026-03-11 | $200 | $200      | $200       | $100          |
      | 2026-03-12 | $0   | $100      | $200       | $200          |
      | 2026-03-13 | $200 | $200      | $500       | $300          |
      | 2026-03-14 | $700 | $300      | $800       | $500          |
      | 2026-03-15 | $600 | $200      | $400       | $600          |
    And the restaurant owner is authenticated
    When the ingredient usage report is requested for the week beginning 2026-03-09
    Then the ingredient usage report should show:
      """
      ...
      """
