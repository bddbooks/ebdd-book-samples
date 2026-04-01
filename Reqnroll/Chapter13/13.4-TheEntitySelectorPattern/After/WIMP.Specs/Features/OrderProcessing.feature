Feature: Order Processing

Rule: Kitchen staff should work on orders in the order they arrive

Scenario: Start work on orders in order of arrival
  Given the following orders have been placed
    | Placed At |
    | 13:33:47  |
    | 13:45:30  |
  When a kitchen staff member asks for an order to work on
  Then the earliest order received should be taken
