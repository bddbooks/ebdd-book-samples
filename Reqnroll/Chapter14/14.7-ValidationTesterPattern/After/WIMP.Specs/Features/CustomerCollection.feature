Feature: Customer collection

Rule: Customers must supply acceptable contact details when placing an order for customer-collection
  Scenario Outline: Contact details supplied
    Given a customer has chosen to collect their order
    When the customer provides valid contact details, but:
      | field   | value   |
      | <field> | <value> |
    Then the contact details are <result>

  Examples:
    | description        | field | value         | result       |
    | name missing       | Name  |               | not accepted |
    | wrong email format | Email | invalid-email | not accepted |
    | email is optional  | Email |               | accepted     |

  Scenario Outline: State is mandatory for US customers
    Given a customer has chosen to collect their order
    When the customer provides valid contact details, but:
      | Country   | State   |
      | <country> | <state> |
    Then the contact details are <result>

  Examples:
    | description            | country | state | result       |
    | state missing for US   | US      | -     | not accepted |
    | state provided for US  | US      | CA    | accepted     |
    | state optional, non-US | France  | -     | accepted     |
