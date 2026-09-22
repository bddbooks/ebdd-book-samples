Feature: Order Processing

Rule: Kitchen staff should work on orders in the order they arrive

Scenario: Start work on orders in order of arrival
  Given the following orders have been placed
    | placed at |
    | 13:33:47  |
    | 13:45:30  |
  When a kitchen staff member asks for an order to work on
  Then the order placed at 13:33:47 should be taken
