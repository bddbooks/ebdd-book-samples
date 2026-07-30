Feature: Pricing

Scenario: Price calculation
  Given the net price of the Margherita pizza is [CUR]8
  When the price of 3 Margherita pizzas are calculated
  Then the sum net price is [CUR]24
  And the sales tax is [TAX] percent
