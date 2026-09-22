Feature: Ordering

Rule: More than 4 large pizzas cannot be delivered as a single order
  Scenario: Five large pizzas are ordered
    When the customer places an order for 5 pizzas of size 14"
    Then the order should be rejected with message "We cannot deliver 5 large pizzas in a single order, the maximum is 4"
