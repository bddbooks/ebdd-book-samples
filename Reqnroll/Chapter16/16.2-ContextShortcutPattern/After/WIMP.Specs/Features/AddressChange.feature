Feature: Address Change

Rule: Allow address change if the order has not been picked up yet

  Scenario: Order waiting for pickup
    Given the customer has an order that is waiting for pickup
    When they attempt to change the delivery address
    Then the address change should be allowed
