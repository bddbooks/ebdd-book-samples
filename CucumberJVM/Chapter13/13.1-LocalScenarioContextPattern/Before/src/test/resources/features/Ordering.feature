Feature: Order Cancellation

  Rule: A customer should receive a notification when their order is cancelled

    Scenario: The customer is notified about an order cancellation
      Given the customer Rebecca has logged in
      And the customer Rebecca has placed the order #12342
      When the customer Rebecca cancels the order #12342
      Then the customer Rebecca should receive a notification about the cancellation
