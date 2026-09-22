Feature: Ordering

Rule: Open orders can be cancelled

  Scenario: Customer cancels one of their open orders
    Given the customer has the following orders
      | order no | status    |
      | 1        | Completed |
      | 2        | Placed    |
      | 3        | Placed    |
    And they are authenticated
    When they cancel order 2
    Then their order list should contain
      | order no | status    |
      | 1        | Completed |
      | 2        | Cancelled |
      | 3        | Placed    |
