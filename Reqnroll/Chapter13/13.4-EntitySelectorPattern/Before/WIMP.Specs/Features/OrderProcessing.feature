Feature: Order Processing

Rule: Kitchen staff should work on orders in the order they arrive

Scenario: Start work on orders in order of arrival
  Given the following orders have been placed
    | Order number | Placed At |
    | 240578       | 13:33:47  |
    | 259827       | 13:45:30  |
  When a kitchen staff member asks for an order to work on
  Then the order #240578 should be taken
