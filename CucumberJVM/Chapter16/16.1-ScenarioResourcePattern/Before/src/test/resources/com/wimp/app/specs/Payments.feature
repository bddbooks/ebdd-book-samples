Feature: Payments

Rule: Card payments must be authorised by payment gateway

  Scenario: Customer is shown payment reference
    Given an authenticated customer has placed an order
    When their payment is authorised by the payment gateway
    Then they should be shown a success message with the payment reference
