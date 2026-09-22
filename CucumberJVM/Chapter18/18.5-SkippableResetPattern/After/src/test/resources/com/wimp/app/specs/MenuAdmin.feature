Feature: Menu Admin

Rule: Restaurant owner can add new items to the menu

  # This scenario modifies the MENU table
  Scenario: Restaurant owner adds a new menu item
    Given the restaurant menu is
      | name       | price |
      | Margherita |  7.99 |
      | Pepperoni  |  9.99 |
    When the restaurant owner adds a "BBQ" pizza to the menu for $8.99
    Then the following items should be on the menu
      | name       | price |
      | Margherita |  7.99 |
      | Pepperoni  |  9.99 |
      | BBQ        |  8.99 |

