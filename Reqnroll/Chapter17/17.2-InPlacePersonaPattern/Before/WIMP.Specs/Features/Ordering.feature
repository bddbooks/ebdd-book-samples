Feature: Ordering

Rule: Open orders can be cancelled

  Scenario: Customer cancels one of their open orders
    Given the customer has the following orders
      | order no | status    |
      | 1000     | Completed |
      | 1001     | Placed    |
      | 1002     | Placed    |
    And they have logged in
    When they cancel order 1001
    Then their order list should contain
      | order no | status    |
      | 1000     | Completed |
      | 1001     | Cancelled |
      | 1002     | Placed    |
