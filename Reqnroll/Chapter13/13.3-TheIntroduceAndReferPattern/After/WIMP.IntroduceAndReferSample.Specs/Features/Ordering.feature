Feature: Order Cancellation

Rule: A customer should receive a notification when their order is cancelled

Scenario: The customer is notified about an order cancellation
  Given the customer Rebecca has logged in
  And the logged in customer has placed an order
  When the logged in customer cancels the placed order
  Then the logged in customer should receive a notification about the cancellation
