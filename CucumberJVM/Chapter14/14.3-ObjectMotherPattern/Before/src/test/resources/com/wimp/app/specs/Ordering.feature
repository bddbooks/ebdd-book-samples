Feature: Pizza Ordering with Promotions

Rule: A 25% discount coupon is sent for the next purchase when Margherita pizza is ordered on a "Margherita Friday"
  Scenario: A Margherita Friday coupon is sent
    Given the "Margherita Friday" promotion is active
    And the customer has placed an order containing a "Margherita" pizza
    When the order is delivered
    Then the customer should receive a "MARGHERITA25" coupon via email

Rule: More than 4 large pizzas cannot be delivered as a single order
  Scenario: Five large pizzas are ordered
    When the customer places an order for 5 pizzas of size "Large"
    Then the order should be rejected

Rule: Allow address change if not picked up yet
  Scenario: Order waiting for pickup
    Given the customer has placed an order
    And the order is waiting for pickup
    When the customer requests to change the delivery address
    Then the address change should be allowed
