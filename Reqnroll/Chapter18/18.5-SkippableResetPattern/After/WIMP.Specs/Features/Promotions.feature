Feature: Promotions

Rule: Items above $8 offered with $1 discount as part of the "Friday $1 off" promotion

  # This scenario requires custom restaurant menu
  Scenario: Items participating in promotion are offered
    Given the restaurant menu is
      | name        | price |
      | Margherita  |  7.99 |
      | Pepperoni   |  9.99 |
      | Capricciosa |  8.99 |
    When the customer chooses "Friday $1 off" promotion
    Then the following items should be offered
      | name        | price | original price |
      | Capricciosa |  7.99 |           8.99 |
      | Pepperoni   |  8.99 |           9.99 |
