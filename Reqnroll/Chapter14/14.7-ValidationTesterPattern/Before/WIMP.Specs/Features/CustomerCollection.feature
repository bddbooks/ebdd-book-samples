Feature: Customer collection

Rule: Customers must supply acceptable contact details when placing an order for customer-collection
  Scenario Outline: Contact details supplied
    Given a customer has chosen to collect their order
    When the customer provides the contact details as:
      | name   | email   | phone   |
      | <name> | <email> | <phone> |
    Then the contact details are <result>
  Examples:
    | description                  | name    | email            | phone         | result       |
    | Everything provided          | Rebecca | becca@galaxy.uni |      12334456 | accepted     |
    | No Name                      |         |                  |               | not accepted |
    | No Name, but email and phone |         | becca@galaxy.uni |      12334456 | not accepted |
    | No Name, but phone           |         |                  |      12334456 | not accepted |
    | Only phone                   | Rebecca |                  |      12334456 | accepted     |
    | Only email                   | Rebecca | becca@galaxy.uni |               | accepted     |
    | Only name                    | Rebecca |                  |               | not accepted |
    | Invalid email                | Rebecca | invalid-email    |      12334456 | not accepted |
    | Invalid phone                | Rebecca | becca@galaxy.uni | invalid-phone | not accepted |
    
