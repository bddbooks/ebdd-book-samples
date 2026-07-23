Feature: Ordering

Rule: Open orders can be cancelled

  Scenario: Customer cancels one of their open orders
    Given the customer has the following orders
      | order name | status    |
      | A          | Completed |
      | B          | Placed    |
      | C          | Placed    |
    And they have logged in
    When they cancel order B
    Then their order list should contain
      | order name | status    |
      | A          | Completed |
      | B          | Cancelled |
      | C          | Placed    |
