Feature: Pizza Ordering

Rule: More than 4 large pizzas cannot be delivered as a single order
  Scenario: Five large pizzas are ordered
    When the customer places an order for 5 pizzas of size "Large"
    Then the order should be rejected
