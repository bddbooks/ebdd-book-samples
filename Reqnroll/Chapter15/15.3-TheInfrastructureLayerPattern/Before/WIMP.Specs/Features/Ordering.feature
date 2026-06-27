Feature: Ordering

Rule: Customers receive a notification when their order is cancelled

  Scenario: The customer is notified when they cancel an order
    Given the customer has authenticated
    And they have placed an order
    When they cancel the placed order
    Then they should receive a notification about the cancellation
