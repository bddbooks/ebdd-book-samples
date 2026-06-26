Feature: Restaurant menu

Rule: Menu items can filtered
  Scenario: Menu is filtered for price range
    Given the restaurant menu is
      | name        | price |
      | Margherita  |  7.99 |
      | Pepperoni   |  9.99 |
      | Capricciosa |  8.99 |
    When the customer filters the menu for price range between 8.00 and 9.00
    Then the filtered result should contain only the pizza item "Capricciosa"

  Scenario: Menu is filtered for max calories
    Given the restaurant menu is
      | name        | price | calories |
      | Margherita  |  7.99 |      900 |
      | Pepperoni   |  9.99 |     1200 |
      | Capricciosa |  8.99 |     1100 |
    When the customer filters the menu for maximum calories 1000
    Then the filtered result should contain only the pizza item "Margherita"
