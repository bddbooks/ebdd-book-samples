Feature: Order Cancellation

Rule: A customer should receive a notification when their order is cancelled

Scenario: The customer is notified about an order cancellation
  Given the customer "Rebecca" is authenticated
  And the authenticated customer has placed an order
  When the authenticated customer cancels the placed order
  Then the authenticated customer should receive a notification about the cancellation
