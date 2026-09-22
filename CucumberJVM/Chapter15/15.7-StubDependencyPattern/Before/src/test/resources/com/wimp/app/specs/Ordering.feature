Feature: Ordering

Rule: Customer should be informed about delivery delays

  Scenario: The customer is notified about a delay
    Given the customer is authenticated
    And they have placed an order with
      | expected delivery time |
      | 18:45                  |
    When the delivery has not been made by 18:46
    Then the customer should receive a notification about the delay
