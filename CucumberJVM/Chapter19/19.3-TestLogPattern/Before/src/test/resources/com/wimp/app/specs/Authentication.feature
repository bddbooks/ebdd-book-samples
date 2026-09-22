Feature: Authentication

Rule: Customer needs valid password for login

  Scenario: A registered customer logs in successfully
    When the customer attempts to log in with valid password
    Then they should be authenticated
