Feature: Pricing

Scenario: Price calculation
  Given the net price of the Margherita pizza is $8
  When the price of 3 Margherita pizzas are calculated
  Then the sum net price is $24
  And the sales tax is 6 percent
