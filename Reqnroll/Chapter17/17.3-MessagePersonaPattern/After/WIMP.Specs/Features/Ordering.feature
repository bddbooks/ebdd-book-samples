Feature: Ordering

Rule: More than 4 large pizzas cannot be delivered as a single order
  Scenario: Five large pizzas are ordered
    When the customer places an order for 5 pizzas of size large
    Then the order should be rejected with message [cannot-deliver-too-many-large-pizzas,5]
