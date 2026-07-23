Feature: Customer Collection

Rule: Any visitor to the website can place a customer-collection order

  Scenario: Authenticated customer chooses to collect order
    Given the customer is authenticated
    When they choose to collect their order
    Then they should be asked to confirm contact details

Rule: A collection receipt has to be printed for customer-collection orders

  Scenario: The number of pizzas to be handed over is on the receipt
    Given the customer is authenticated
    And they have placed an order for 3 pizzas
    When they choose to collect their order
    Then a collection receipt should be printed with
      | boxes to be collected |
      | 3                     |
