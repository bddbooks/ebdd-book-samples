Feature: Ordering

Rule: A 25% discount coupon is sent for the next purchase when Margherita pizza is ordered on a "Margherita Friday"

  # This scenario assumes that a pizza named "Margherita" exists, that is ensured by the step definition of 'And the customer has placed an order...'
  Scenario: A Margherita Friday coupon is sent
    Given the "Margherita Friday" promotion is active
    And the customer has placed an order containing a "Margherita" pizza
    When the order is delivered
    Then the customer should receive a "MARGHERITA25" coupon via email
