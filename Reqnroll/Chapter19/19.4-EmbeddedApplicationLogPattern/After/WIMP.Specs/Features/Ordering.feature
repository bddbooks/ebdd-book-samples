Feature: Ordering

Rule: More than 4 large pizzas cannot be delivered as a single order

  @log:debug
  Scenario: Five large pizzas are ordered
    Given the customer is authenticated
    When they place an order for 5 pizzas of size large
    Then the order should be rejected with message "We cannot deliver 5 large pizzas in a single order, the maximum is 4"
